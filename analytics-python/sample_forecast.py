import pandas as pd
import matplotlib.pyplot as plt

data = {
    "Month": ["Jan", "Feb", "Mar", "Apr", "May"],
    "Stock_Used": [120, 135, 150, 165, 180]
}

df = pd.DataFrame(data)

print("Inventory Usage Data:")
print(df)

plt.plot(df["Month"], df["Stock_Used"], marker="o")
plt.title("Inventory Usage Trend")
plt.xlabel("Month")
plt.ylabel("Units Used")
plt.grid(True)

plt.savefig("inventory_trend.png")
print("Chart saved as inventory_trend.png")