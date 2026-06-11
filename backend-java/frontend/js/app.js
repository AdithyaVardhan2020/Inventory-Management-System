const API = 'http://localhost:8080';

let categoryChart = null;
let statsChart = null;
let forecastChart = null;
let stockForecastChart = null;
let editingProductId = null;
let editingSupplierId = null;
const reportCache = {};

async function api(path, options = {}) {
    const res = await fetch(API + path, {
        headers: { 'Content-Type': 'application/json', ...options.headers },
        ...options
    });
    const text = await res.text();
    let data = null;
    if (text) {
        try { data = JSON.parse(text); } catch { data = text; }
    }
    if (!res.ok) {
        const msg = data?.message || `Request failed (${res.status})`;
        throw new Error(msg);
    }
    return data;
}

function showStatus(el, message, type) {
    el.textContent = message;
    el.className = 'status ' + type;
}

function formToJson(form, numericFields = []) {
    const data = {};
    new FormData(form).forEach((v, k) => {
        data[k] = numericFields.includes(k) ? Number(v) : v;
    });
    return data;
}

function formatPrice(n) {
    return '₹' + Number(n).toLocaleString('en-IN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });
}

function navigateTo(panelId) {
    document.querySelectorAll('.panel').forEach(p => p.classList.remove('active'));
    document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));
    document.getElementById(panelId)?.classList.add('active');
    document.querySelector(`[data-panel="${panelId}"]`)?.classList.add('active');
    document.getElementById('sidebar')?.classList.remove('open');
}

function fillTable(tbody, rows, cols, renderCell) {
    if (!rows.length) {
        tbody.innerHTML = `<tr><td colspan="${cols.length}" class="empty-state">No records found</td></tr>`;
        return;
    }
    tbody.innerHTML = rows.map(row => {
        const cells = cols.map(col => {
            if (renderCell && renderCell[col]) return renderCell[col](row);
            return `<td>${row[col] ?? ''}</td>`;
        }).join('');
        return `<tr>${cells}</tr>`;
    }).join('');
}

async function loadStatistics() {
    const stats = await api('/api/statistics');
    document.getElementById('total-products').textContent = stats.totalProducts;
    document.getElementById('low-stock-count').textContent = stats.lowStockCount;
    document.getElementById('total-suppliers').textContent = stats.totalSuppliers;
    document.getElementById('total-transactions').textContent = stats.totalTransactions;
    document.getElementById('total-inventory-value').textContent = formatPrice(stats.totalInventoryValue || 0);
    document.getElementById('total-sales-value').textContent = formatPrice(stats.totalSalesValue || 0);
    document.getElementById('data-source-label').textContent =
        'Data source: ' + (stats.dataSource || 'Amazon India public datasets');

    renderCategoryChart(stats.categoryBreakdown || []);
    renderStatsChart(stats);
    return stats;
}

function renderCategoryChart(breakdown) {
    const ctx = document.getElementById('category-chart');
    if (!ctx) return;
    const labels = breakdown.map(c => c.category || 'Unknown');
    const values = breakdown.map(c => c.count);
    if (categoryChart) categoryChart.destroy();
    categoryChart = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels,
            datasets: [{
                data: values,
                backgroundColor: ['#1e3a5f', '#3b82f6', '#16a34a', '#d97706', '#8b5cf6', '#ec4899']
            }]
        },
        options: { responsive: true, plugins: { legend: { position: 'bottom' } } }
    });
}

function renderStatsChart(stats) {
    const ctx = document.getElementById('stats-chart');
    if (!ctx) return;
    if (statsChart) statsChart.destroy();
    statsChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: ['Products', 'Suppliers', 'Transactions', 'Low Stock'],
            datasets: [{
                label: 'Count',
                data: [stats.totalProducts, stats.totalSuppliers, stats.totalTransactions, stats.lowStockCount],
                backgroundColor: ['#1e3a5f', '#3b82f6', '#16a34a', '#d97706']
            }]
        },
        options: {
            responsive: true,
            plugins: { legend: { display: false } },
            scales: { y: { beginAtZero: true, ticks: { stepSize: 1 } } }
        }
    });
}

async function loadProducts() {
    const keyword = document.getElementById('product-search')?.value || '';
    const category = document.getElementById('product-category-filter')?.value || '';
    let path = '/products/all';
    if (keyword || category) {
        const params = new URLSearchParams();
        if (keyword) params.set('keyword', keyword);
        if (category) params.set('category', category);
        path = '/products/search?' + params.toString();
    }
    const products = await api(path);
    const tbody = document.querySelector('#products-table tbody');
    fillTable(tbody, products,
        ['productId', 'productName', 'category', 'quantity', 'price', 'reorderLevel', 'supplierName', 'actions'],
        {
            price: r => `<td>${formatPrice(r.price)}</td>`,
            quantity: r => `<td>${r.quantity}${r.quantity <= r.reorderLevel ? ' <span class="badge badge-danger">Low</span>' : ''}</td>`,
            actions: r => `<td class="actions">
                <button class="btn btn-outline btn-sm" onclick="editProduct(${r.productId})">Edit</button>
                <button class="btn btn-danger btn-sm" onclick="deleteProduct(${r.productId})">Delete</button>
            </td>`
        }
    );
    return products;
}

async function loadCategories() {
    const categories = await api('/products/categories');
    const select = document.getElementById('product-category-filter');
    if (!select) return;
    const current = select.value;
    select.innerHTML = '<option value="">All Categories</option>' +
        categories.map(c => `<option value="${c}">${c}</option>`).join('');
    select.value = current;
}

async function loadLowStock() {
    const items = await api('/products/low-stock');
    const tbody = document.querySelector('#low-stock-table tbody');
    fillTable(tbody, items, ['productId', 'productName', 'category', 'quantity', 'reorderLevel']);
    const alertList = document.getElementById('alert-list');
    const alertListFull = document.getElementById('alert-list-full');
    const html = items.length
        ? items.map(p => `<li><strong>${p.productName}</strong> — ${p.quantity} units left (reorder at ${p.reorderLevel})</li>`).join('')
        : '<li class="empty-state">All stock levels are healthy</li>';
    if (alertList) alertList.innerHTML = html;
    if (alertListFull) alertListFull.innerHTML = html;
    return items;
}

async function loadSuppliers() {
    const keyword = document.getElementById('supplier-search')?.value || '';
    const path = keyword ? `/suppliers/search?keyword=${encodeURIComponent(keyword)}` : '/suppliers/all';
    const suppliers = await api(path);
    const tbody = document.querySelector('#suppliers-table tbody');
    fillTable(tbody, suppliers,
        ['supplierId', 'supplierName', 'contactPerson', 'phone', 'email', 'actions'],
        {
            actions: r => `<td class="actions">
                <button class="btn btn-outline btn-sm" onclick="editSupplier(${r.supplierId})">Edit</button>
                <button class="btn btn-danger btn-sm" onclick="deleteSupplier(${r.supplierId})">Delete</button>
            </td>`
        }
    );
    populateSupplierSelect(suppliers);
    return suppliers;
}

function populateSupplierSelect(suppliers) {
    const select = document.getElementById('product-supplierId');
    if (!select) return;
    select.innerHTML = suppliers.map(s =>
        `<option value="${s.supplierId}">${s.supplierId} — ${s.supplierName}</option>`
    ).join('');
}

async function loadTransactions() {
    const transactions = await api('/transactions/all');
    const tbody = document.querySelector('#transactions-table tbody');
    fillTable(tbody, transactions,
        ['transactionId', 'productName', 'transactionType', 'quantity', 'transactionDate', 'notes']
    );
    return transactions;
}

async function loadAnalytics() {
    const [forecast, reorder] = await Promise.all([
        api('/api/analytics/forecast'),
        api('/api/analytics/reorder-suggestions')
    ]);

    const summary = document.getElementById('forecast-summary');
    if (summary) {
        summary.innerHTML = `
            <div class="forecast-stat"><div class="label">Total Products</div><div class="num">${forecast.totalProducts}</div></div>
            <div class="forecast-stat"><div class="label">Low Stock</div><div class="num">${forecast.lowStockCount}</div></div>
            <div class="forecast-stat"><div class="label">Forecasted Demand</div><div class="num">${forecast.forecastedMonthlyDemand}</div></div>
            <div class="forecast-stat"><div class="label">Categories</div><div class="num">${forecast.categoryForecasts.length}</div></div>`;
    }

    renderForecastCharts(forecast);

    const tbody = document.querySelector('#reorder-table tbody');
    if (tbody) {
        fillTable(tbody, reorder,
            ['productName', 'category', 'currentQuantity', 'reorderLevel', 'suggestedOrderQuantity', 'priority', 'supplierName'],
            {
                currentQuantity: r => `<td>${r.currentQuantity}</td>`,
                priority: r => `<td><span class="badge badge-${r.priority.toLowerCase()}">${r.priority}</span></td>`
            }
        );
    }
}

function renderForecastCharts(forecast) {
    const items = forecast.categoryForecasts || [];
    const labels = items.map(i => i.category);
    const demand = items.map(i => i.forecastedDemand);
    const stock = items.map(i => i.currentStock);

    const ctx1 = document.getElementById('forecast-chart');
    if (ctx1) {
        if (forecastChart) forecastChart.destroy();
        forecastChart = new Chart(ctx1, {
            type: 'bar',
            data: { labels, datasets: [{ label: 'Forecasted Demand', data: demand, backgroundColor: '#3b82f6' }] },
            options: { responsive: true, plugins: { legend: { display: false } }, scales: { y: { beginAtZero: true } } }
        });
    }

    const ctx2 = document.getElementById('stock-forecast-chart');
    if (ctx2) {
        if (stockForecastChart) stockForecastChart.destroy();
        stockForecastChart = new Chart(ctx2, {
            type: 'line',
            data: {
                labels,
                datasets: [
                    { label: 'Current Stock', data: stock, borderColor: '#1e3a5f', backgroundColor: 'rgba(30,58,95,.1)', fill: true },
                    { label: 'Forecasted Demand', data: demand, borderColor: '#d97706', borderDash: [5, 5] }
                ]
            },
            options: { responsive: true, scales: { y: { beginAtZero: true } } }
        });
    }
}

async function fetchReport(type) {
    if (reportCache[type]) return reportCache[type];
    const report = await api('/api/reports/' + type);
    reportCache[type] = report;
    return report;
}

function displayReport(report) {
    const preview = document.getElementById('report-preview');
    preview.classList.remove('hidden');
    document.getElementById('report-title').textContent = report.title;
    document.getElementById('report-meta').textContent =
        `Generated: ${report.generatedAt} | Records: ${report.recordCount}`;

    const thead = document.querySelector('#report-table thead');
    const tbody = document.querySelector('#report-table tbody');
    thead.innerHTML = '<tr>' + report.columns.map(c => `<th>${c}</th>`).join('') + '</tr>';
    if (!report.rows.length) {
        tbody.innerHTML = `<tr><td colspan="${report.columns.length}" class="empty-state">No data</td></tr>`;
        return;
    }
    tbody.innerHTML = report.rows.map(row =>
        '<tr>' + report.columns.map(c => `<td>${row[c] ?? ''}</td>`).join('') + '</tr>'
    ).join('');
    preview.scrollIntoView({ behavior: 'smooth' });
}

async function viewReport(type) {
    try {
        reportCache[type] = null;
        const report = await fetchReport(type);
        displayReport(report);
    } catch (e) {
        alert('Failed to load report: ' + e.message);
    }
}

function exportToCsv(report) {
    const header = report.columns.join(',');
    const rows = report.rows.map(row =>
        report.columns.map(c => {
            const val = row[c] ?? '';
            const str = String(val).replace(/"/g, '""');
            return str.includes(',') ? `"${str}"` : str;
        }).join(',')
    );
    const csv = [header, ...rows].join('\n');
    const blob = new Blob([csv], { type: 'text/csv' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `${report.reportType}-report-${new Date().toISOString().slice(0, 10)}.csv`;
    a.click();
    URL.revokeObjectURL(url);
}

async function exportReport(type) {
    try {
        reportCache[type] = null;
        const report = await fetchReport(type);
        exportToCsv(report);
    } catch (e) {
        alert('Failed to export report: ' + e.message);
    }
}

async function loadDashboard() {
    try {
        await Promise.all([
            loadStatistics(),
            loadProducts(),
            loadCategories(),
            loadLowStock(),
            loadSuppliers(),
            loadTransactions(),
            loadAnalytics()
        ]);
    } catch (e) {
        console.error(e);
        alert('Could not load dashboard: ' + e.message);
    }
}

async function editProduct(id) {
    const product = await api('/products/' + id);
    editingProductId = id;
    const form = document.getElementById('product-form');
    form.productName.value = product.productName;
    form.category.value = product.category;
    form.quantity.value = product.quantity;
    form.price.value = product.price;
    form.reorderLevel.value = product.reorderLevel;
    form.supplierId.value = product.supplierId;
    document.getElementById('product-form-title').textContent = 'Update Product';
    document.getElementById('product-submit-btn').textContent = 'Update Product';
    document.getElementById('product-cancel-btn').classList.remove('hidden');
    navigateTo('panel-products');
    form.scrollIntoView({ behavior: 'smooth' });
}

function resetProductForm() {
    editingProductId = null;
    const form = document.getElementById('product-form');
    form.reset();
    document.getElementById('product-form-title').textContent = 'Add Product';
    document.getElementById('product-submit-btn').textContent = 'Add Product';
    document.getElementById('product-cancel-btn').classList.add('hidden');
}

async function deleteProduct(id) {
    if (!confirm('Delete this product?')) return;
    try {
        await api('/products/delete/' + id, { method: 'DELETE' });
        loadDashboard();
    } catch (e) {
        alert(e.message);
    }
}

async function editSupplier(id) {
    const supplier = await api('/suppliers/' + id);
    editingSupplierId = id;
    const form = document.getElementById('supplier-form');
    form.supplierName.value = supplier.supplierName;
    form.contactPerson.value = supplier.contactPerson;
    form.phone.value = supplier.phone;
    form.email.value = supplier.email;
    form.address.value = supplier.address;
    document.getElementById('supplier-form-title').textContent = 'Update Supplier';
    document.getElementById('supplier-submit-btn').textContent = 'Update Supplier';
    document.getElementById('supplier-cancel-btn').classList.remove('hidden');
    navigateTo('panel-suppliers');
}

function resetSupplierForm() {
    editingSupplierId = null;
    const form = document.getElementById('supplier-form');
    form.reset();
    document.getElementById('supplier-form-title').textContent = 'Add Supplier';
    document.getElementById('supplier-submit-btn').textContent = 'Add Supplier';
    document.getElementById('supplier-cancel-btn').classList.add('hidden');
}

async function deleteSupplier(id) {
    if (!confirm('Delete this supplier?')) return;
    try {
        await api('/suppliers/delete/' + id, { method: 'DELETE' });
        loadDashboard();
    } catch (e) {
        alert(e.message);
    }
}

function initDashboard() {
    document.querySelectorAll('.nav-item').forEach(btn => {
        btn.addEventListener('click', () => {
            navigateTo(btn.dataset.panel);
            if (btn.dataset.panel === 'panel-analytics') loadAnalytics();
        });
    });

    document.getElementById('menu-toggle')?.addEventListener('click', () => {
        document.getElementById('sidebar').classList.toggle('open');
    });

    document.getElementById('logout-btn')?.addEventListener('click', () => {
        sessionStorage.removeItem('loggedIn');
        window.location.href = 'login.html';
    });

    document.getElementById('product-search')?.addEventListener('input', debounce(loadProducts, 300));
    document.getElementById('product-category-filter')?.addEventListener('change', loadProducts);
    document.getElementById('supplier-search')?.addEventListener('input', debounce(loadSuppliers, 300));

    document.getElementById('product-form')?.addEventListener('submit', async (e) => {
        e.preventDefault();
        const status = document.getElementById('product-form-status');
        const body = formToJson(e.target, ['quantity', 'price', 'reorderLevel', 'supplierId']);
        try {
            if (editingProductId) {
                await api('/products/update/' + editingProductId, { method: 'PUT', body: JSON.stringify(body) });
                showStatus(status, 'Product updated successfully', 'success');
                resetProductForm();
            } else {
                await api('/products/add', { method: 'POST', body: JSON.stringify(body) });
                showStatus(status, 'Product added successfully', 'success');
                e.target.reset();
            }
            loadDashboard();
        } catch (err) {
            showStatus(status, err.message, 'error');
        }
    });

    document.getElementById('product-cancel-btn')?.addEventListener('click', resetProductForm);

    document.getElementById('supplier-form')?.addEventListener('submit', async (e) => {
        e.preventDefault();
        const status = document.getElementById('supplier-form-status');
        const body = formToJson(e.target);
        try {
            if (editingSupplierId) {
                await api('/suppliers/update/' + editingSupplierId, { method: 'PUT', body: JSON.stringify(body) });
                showStatus(status, 'Supplier updated successfully', 'success');
                resetSupplierForm();
            } else {
                await api('/suppliers/add', { method: 'POST', body: JSON.stringify(body) });
                showStatus(status, 'Supplier added successfully', 'success');
                e.target.reset();
            }
            loadDashboard();
        } catch (err) {
            showStatus(status, err.message, 'error');
        }
    });

    document.getElementById('supplier-cancel-btn')?.addEventListener('click', resetSupplierForm);

    if (sessionStorage.getItem('loggedIn') !== 'true') {
        window.location.href = 'login.html';
        return;
    }

    navigateTo('panel-overview');
    loadDashboard();
}

function debounce(fn, ms) {
    let t;
    return (...args) => { clearTimeout(t); t = setTimeout(() => fn(...args), ms); };
}

function validateLoginForm() {
    const user = document.getElementById('username');
    const pass = document.getElementById('password');
    let valid = true;
    document.querySelectorAll('.field-error').forEach(el => el.remove());
    if (!user.value.trim()) {
        showFieldError(user, 'Username is required');
        valid = false;
    }
    if (!pass.value) {
        showFieldError(pass, 'Password is required');
        valid = false;
    } else if (pass.value.length < 4) {
        showFieldError(pass, 'Password must be at least 4 characters');
        valid = false;
    }
    return valid;
}

function showFieldError(input, message) {
    input.classList.add('invalid');
    const err = document.createElement('div');
    err.className = 'field-error';
    err.textContent = message;
    input.parentElement.appendChild(err);
}

function initLogin() {
    document.getElementById('login-form')?.addEventListener('submit', async (e) => {
        e.preventDefault();
        document.querySelectorAll('.field-error').forEach(el => el.remove());
        document.querySelectorAll('input.invalid').forEach(el => el.classList.remove('invalid'));
        if (!validateLoginForm()) return;

        const status = document.getElementById('login-status');
        showStatus(status, 'Logging in...', '');
        try {
            const res = await fetch(API + '/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    username: document.getElementById('username').value,
                    password: document.getElementById('password').value
                })
            });
            const text = await res.text();
            if (text === 'Login successful') {
                sessionStorage.setItem('loggedIn', 'true');
                showStatus(status, 'Login successful! Redirecting...', 'success');
                setTimeout(() => window.location.href = 'dashboard.html', 500);
            } else {
                showStatus(status, text || 'Invalid credentials', 'error');
            }
        } catch {
            showStatus(status, 'Cannot reach backend at ' + API, 'error');
        }
    });

    if (sessionStorage.getItem('loggedIn') === 'true') {
        window.location.href = 'dashboard.html';
    }
}
