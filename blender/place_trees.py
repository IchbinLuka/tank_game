import bpy
import random

context = bpy.context
scene = context.scene

tree = bpy.data.objects["Tree"]

NUM_TREES = 10

for _ in range(NUM_TREES):
    new_tree = tree.copy()
    new_tree.data = tree.data.copy()
    x, z = random.random() * 100, random.random() * 100
    new_tree.transform.translate(value=(x, 0, z))
