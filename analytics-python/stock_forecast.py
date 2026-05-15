import pandas as pd
import matplotlib.pyplot as plt

df = pd.read_csv("analytics-python/retail_store_inventory.csv")

df["Date"] = pd.to_datetime(df["Date"])

monthly_data = df.groupby(df["Date"].dt.to_period("M")).agg({
    "Units Sold": "sum",
    "Inventory Level": "mean",
    "Demand Forecast": "mean",
    "Units Ordered": "sum"
}).reset_index()

monthly_data["Date"] = monthly_data["Date"].astype(str)

average_units_sold = monthly_data["Units Sold"].mean()
last_month_units_sold = monthly_data["Units Sold"].iloc[-1]
next_month_forecast = (average_units_sold + last_month_units_sold) / 2

print("Inventory Forecast Report")
print("-------------------------")
print(monthly_data)
print()
print(f"Average Monthly Units Sold: {average_units_sold:.2f}")
print(f"Last Month Units Sold: {last_month_units_sold:.2f}")
print(f"Next Month Forecasted Units Sold: {next_month_forecast:.2f}")

plt.figure(figsize=(10, 6))
plt.plot(monthly_data["Date"], monthly_data["Units Sold"], marker="o")
plt.title("Monthly Units Sold Trend")
plt.xlabel("Month")
plt.ylabel("Units Sold")
plt.xticks(rotation=45)
plt.grid(True)
plt.tight_layout()
plt.savefig("inventory_trend.png")
plt.show()

low_inventory = df[df["Inventory Level"] <= df["Demand Forecast"]]

print()
print("Low Inventory Alert Records:")
print("----------------------------")
print(low_inventory[[
    "Date",
    "Store ID",
    "Product ID",
    "Category",
    "Region",
    "Inventory Level",
    "Demand Forecast",
    "Units Ordered"
]].head(20))