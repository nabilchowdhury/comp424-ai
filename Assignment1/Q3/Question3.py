import numpy as np
import matplotlib.pyplot as plt
import random

GRANULARITY = 1000
LB = 0
UB = 10

x_axis = np.arange(LB, UB + (UB - LB) / GRANULARITY, (UB - LB) / GRANULARITY)
y_axis = np.sin(np.square(x_axis) / 2) / np.log2(x_axis + 4)

plt.figure(1)
plt.plot(x_axis, y_axis)
plt.show()

def _function(x, LB, UB):
    return np.sin(np.square(x) / 2) / np.log2(x + 4) if LB <= x <= UB else -99999


def hill_climbing(step_size, x):
    EPOCHS = 10000
    cur_x = x
    best_e = _function(cur_x, LB, UB)

    for epoch in range(1, EPOCHS):
        left_neighbor = cur_x - step_size
        right_neighbor = cur_x + step_size
        left_e = _function(left_neighbor, LB, UB)
        right_e = _function(right_neighbor, LB, UB)
        best_neighbor = left_neighbor if left_e > right_e else right_neighbor
        best_neighbor_e = left_e if left_e > right_e else right_e

        if best_neighbor_e <= best_e:
            return step_size, x, epoch, cur_x, best_e

        cur_x = best_neighbor
        best_e = best_neighbor_e

    # (step size, initial_x, iterations, best_x, best_e)
    return step_size, x, EPOCHS, cur_x, best_e


def hill_climbing_test():
    return [hill_climbing(0.01 * step, x) for step in range(1, 11) for x in range(LB, UB + 1)]


def _boltzmann(E, Ei, T):
    return np.exp(-(E - Ei)/T)

def simulated_annealing(step_size, x, alpha=0.01, T=40):
    EPOCHS = 10000
    cur_x = x
    best_e = _function(cur_x, LB, UB)
    best_x = cur_x
    best_e_global = best_e

    for epoch in range(1, EPOCHS):
        neighbor = cur_x + random.choice([-1, 1]) * step_size
        neighbor_e = _function(neighbor, LB, UB)

        if neighbor_e > best_e_global:
            best_e_global = neighbor_e
            best_x = neighbor

        if neighbor_e > best_e:
            best_e = neighbor_e
            best_x = neighbor
            cur_x = neighbor
        else:
            if random.uniform(0, 1) <= _boltzmann(best_e, neighbor_e, T):
                best_e = neighbor_e
                best_x = neighbor
                cur_x = neighbor

        T -= alpha * T

    return best_x


# print(_boltzmann(4, 3, 1))
print(simulated_annealing(0.1, 8))
