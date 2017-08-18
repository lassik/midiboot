#! /usr/bin/env python3

import os
import sys
from contextlib import contextmanager

import tensorflow as tf


@contextmanager
def gen(name):
    name = os.path.join(os.getcwd(), "{}.pb".format(name))
    with open(name, "wb") as out:
        g = tf.Graph()
        with g.as_default():
            yield
        out.write(g.as_graph_def().SerializeToString())


with gen("transpose"):
    pitch_in = tf.placeholder(tf.float32, name="pitch_in")
    pitch_out = tf.constant(1.0) + pitch_in
    tf.identity(pitch_out, name="pitch_out")
