import json
import matplotlib.pyplot as plt
import pandas as pd

plt.style.use('dark_background')

constant_df = pd.read_csv("results/data/constantStrategy.csv")
qlearning_df = pd.read_csv("results/data/qLearningStrategy.csv")

plt.title('Reward = delivered - discarded')
plt.ylabel('Reward')
plt.xlabel('Iteration')

constant_rolling = constant_df.rolling(window=100).mean()
qlearning_rolling = qlearning_df.rolling(window=100).mean()

x_ticks = range(0, len(constant_rolling["reward"]), 2500)

plt.xticks(x_ticks)

plt.plot(constant_rolling["reward"])
plt.plot(qlearning_rolling["reward"])

plt.legend(['Constant', 'Q-Learning'], loc='upper center', bbox_to_anchor=(0.5, -0.20), ncol=2)
plt.tight_layout(pad=1)

plt.savefig(f"results/rewards.png", dpi=400)

