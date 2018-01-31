import numpy as np
import matplotlib.pyplot as plt
import random, secrets

GRANULARITY = 1000
LB = 0
UB = 10

x_axis = np.arange(LB, UB + (UB - LB) / GRANULARITY, (UB - LB) / GRANULARITY)
y_axis = np.sin(np.square(x_axis) / 2) / np.log2(x_axis + 4)

# plt.figure(1)
# plt.plot(x_axis, y_axis)
# plt.show()

def _function(x, LB, UB):
    return np.sin(np.square(x) / 2) / np.log2(x + 4) if LB <= x <= UB else -99999


def hill_climbing(step_size, x):
    EPOCHS = 10000
    cur_x = x
    cur_e = _function(cur_x, LB, UB)

    for epoch in range(1, EPOCHS):
        left_neighbor = cur_x - step_size
        right_neighbor = cur_x + step_size
        left_e = _function(left_neighbor, LB, UB)
        right_e = _function(right_neighbor, LB, UB)
        best_neighbor = left_neighbor if left_e > right_e else right_neighbor
        best_neighbor_e = left_e if left_e > right_e else right_e

        if best_neighbor_e <= cur_e:
            return step_size, x, epoch, cur_x, cur_e

        cur_x = best_neighbor
        cur_e = best_neighbor_e

    # (step size, initial_x, iterations, best_x, best_e)
    return step_size, x, EPOCHS, cur_x, cur_e


def hill_climbing_test():
    return [hill_climbing(0.01 * step, x) for step in range(1, 11) for x in range(LB, UB + 1)]


def _boltzmann(E, Ei, T):
    return np.exp((Ei - E)/T)

def simulated_annealing(step_size=0.01, x=LB, alpha=0.99, T=10):
    cur_x = x
    cur_e = _function(cur_x, LB, UB)
    best_x = cur_x
    best_e = cur_e
    epoch = 1
    while T > 1e-6:
        neighbor = cur_x + (-1 if random.random() < 0.5 else 1) * step_size
        # If out of bounds, we only have one direction to go
        if neighbor < LB or neighbor > UB:
            neighbor = cur_x + step_size if neighbor < LB else cur_x - step_size
        neighbor_e = _function(neighbor, LB, UB)

        if neighbor_e > best_e:
            best_x = neighbor
            best_e = neighbor_e

        if neighbor_e > cur_e or random.random() <= _boltzmann(cur_e, neighbor_e, T):
            cur_x = neighbor
            cur_e = neighbor_e

        epoch += 1
        T *= alpha

    return x, epoch, best_x, best_e



# hill_results = hill_climbing_test()
sim_results = []
for i in range(LB, UB + 1):
    sim_results.append(simulated_annealing(0.02, i, alpha=0.999, T=10000))

best = (1,1,1,-1)
for result in sim_results:
    best = result if result[3] > best[3] else best
    print(result[0], "&", result[1], "&", result[2], "&", result[3], "\\\\")

print()
print(best)
