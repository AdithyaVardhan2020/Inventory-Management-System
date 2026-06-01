import os
import pandas as pd
import matplotlib.pyplot as plt

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
CSV_PATH = os.path.join(SCRIPT_DIR, "sample_data.csv")
OUTPUT_PATH = os.path.join(os.path.dirname(SCRIPT_DIR), "inventory_trend.png")

df = pd.read_csv(CSV_PATH)

average_usage = df["Stock_Used"].mean()
last_month_usage = df["Stock_Used"].iloc[-1]
next_month_forecast = (average_usage + last_month_usage) / 2

categories = {"Electronics": 220, "Groceries": 180, "Furniture": 140}
regions = {"North": 200, "South": 170, "East": 150, "West": 190}
reorder_level = 160

print("Inventory Forecast Report")
print("-------------------------")
print(f"Average Monthly Stock Usage: {average_usage:.2f}")
print(f"Last Month Stock Usage: {last_month_usage}")
print(f"Next Month Forecasted Stock Usage: {next_month_forecast:.2f}")

print("\nCategory-Based Forecasting")
for category, demand in categories.items():
    print(f"{category}: Expected Demand = {demand}")

print("\nRegional Demand Forecasting")
for region, demand in regions.items():
    print(f"{region}: Forecast Demand = {demand}")

print("\nReorder Suggestions")
for category, demand in categories.items():
    if demand > reorder_level:
        print(f"Reorder needed for {category}")

fig, axes = plt.subplots(1, 3, figsize=(14, 4))

axes[0].plot(df["Month"], df["Stock_Used"], marker="o", color="#1e3a5f", linewidth=2)
axes[0].set_title("Monthly Usage Trend")
axes[0].set_xlabel("Month")
axes[0].set_ylabel("Stock Used")
axes[0].tick_params(axis="x", rotation=45)
axes[0].grid(True, alpha=0.3)

cat_names = list(categories.keys())
cat_values = list(categories.values())
axes[1].bar(cat_names, cat_values, color=["#3b82f6", "#16a34a", "#d97706"])
axes[1].set_title("Category Demand Forecast")
axes[1].set_ylabel("Expected Demand")
axes[1].tick_params(axis="x", rotation=30)

region_names = list(regions.keys())
region_values = list(regions.values())
axes[2].barh(region_names, region_values, color="#1e3a5f")
axes[2].set_title("Regional Demand Forecast")
axes[2].set_xlabel("Forecast Demand")

plt.tight_layout()
plt.savefig(OUTPUT_PATH, dpi=150)
print(f"\nChart saved to {OUTPUT_PATH}")
plt.show()
