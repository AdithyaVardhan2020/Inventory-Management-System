import pandas as pd
import matplotlib.pyplot as plt

# Monthly stock usage data
df = pd.read_csv("analytics-python/sample_data.csv")

# Basic forecasting
average_usage = df["Stock_Used"].mean()
last_month_usage = df["Stock_Used"].iloc[-1]
next_month_forecast = (average_usage + last_month_usage) / 2

# Category-based forecasting
categories = {
    "Electronics": 220,
    "Groceries": 180,
    "Furniture": 140
}

# Regional demand forecasting
regions = {
    "North": 200,
    "South": 170,
    "East": 150,
    "West": 190
}

# Reorder suggestions
reorder_level = 160

print("Inventory Forecast Report")
print("-------------------------")
print(df)
print()

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

# Graph
plt.figure(figsize=(8, 5))
plt.plot(df["Month"], df["Stock_Used"], marker="o")

plt.title("Monthly Inventory Usage Trend")
plt.xlabel("Month")
plt.ylabel("Stock Used")

plt.xticks(rotation=45)
plt.grid(True)

plt.tight_layout()

plt.savefig("inventory_trend.png")

plt.show()
