#
#   A script to generate the filters.sqlite database.
#

import itertools
import sqlite3
import sys

TABLE_FILTERS = "filters"

COL_NAME = "name"
COL_IS_BUILT_IN = "is_built_in"
COL_ALGORITHM = "algorithm"
COL_COMPONENT = "component"
COL_ORDER = "ordering"
COL_COMBINE_TYPE = "combine_type"
COL_COMBINE_FUNC_1 = "combine_func_1"
COL_COMBINE_FUNC_2 = "combine_func_2"
COL_COMBINE_FUNC_3 = "combine_func_3"
COL_COMBINE_FUNC_4 = "combine_func_4"
COL_PARTITION_TYPE = "partition_type"
COL_NUM_ROWS = "num_rows"
COL_NUM_COLS = "num_cols"

# sql statement to create the Filters table
DATABASE_CREATE = \
    "CREATE TABLE " + TABLE_FILTERS + "(" \
    + COL_NAME + " TEXT PRIMARY KEY NOT NULL," \
    + COL_IS_BUILT_IN + " INTEGER NOT NULL," \
 \
    + COL_ALGORITHM + " INTEGER NOT NULL," \
    + COL_COMPONENT + " INTEGER NOT NULL," \
    + COL_ORDER + " INTEGER NOT NULL," \
 \
    + COL_COMBINE_TYPE + " INTEGER NOT NULL," \
    + COL_COMBINE_FUNC_1 + " INTEGER NOT NULL," \
    + COL_COMBINE_FUNC_2 + " INTEGER NOT NULL," \
    + COL_COMBINE_FUNC_3 + " INTEGER NOT NULL," \
    + COL_COMBINE_FUNC_4 + " INTEGER NOT NULL," \
 \
    + COL_PARTITION_TYPE + " INTEGER NOT NULL," \
    + COL_NUM_ROWS + " INTEGER NOT NULL," \
    + COL_NUM_COLS + " INTEGER NOT NULL" + ");"

# sql statement to delete the Filters table
DATABASE_DELETE_EXISTING = "DROP TABLE IF EXISTS " + TABLE_FILTERS

# components

CMPNT_RED = 0
CMPNT_GREEN = 1
CMPNT_BLUE = 2
CMPNT_HUE = 3
CMPNT_SAT = 4
CMPNT_VAL = 5
CMPNT_ALPHA = 6

# orders

DESCENDING = 0
ASCENDING = 1

# combo types

COMBINE_ARGB = 0
COMBINE_AHSV = 1

# combo funcs

PRESERVE = 0
REPLACE = 1
ADD = 2
SUBTRACT = 3
MULTIPLY = 4
XOR = 5

# algorithm types

SORT = 0
HEAPIFY = 1
BST = 2

# partition types

GRID_PARTITION = 0


BUILT_IN = 1

ALGORITHMS = [SORT, HEAPIFY, BST]
COMPONENTS = [CMPNT_BLUE, CMPNT_VAL]
ORDERS = [DESCENDING]
COMBO_TYPES = [COMBINE_ARGB]
COMBO_FUNCS = [REPLACE, ADD]
PARTITION_TYPES = [GRID_PARTITION]
GRID_DIMS = [1, 3, 4]

GRID_DIMS_NEW = [[1, 1], [4, 3]]


def name_filters(values):
    name = "".join((str(val) for val in values))
    return tuple([name] + list(values))


def add_dims(values, new_dims):
    return tuple(values + new_dims)

def main(argv):

    # open database
    connection = sqlite3.connect(argv[0])
    cursor = connection.cursor()

    # delete any existing entries
    cursor.execute(DATABASE_DELETE_EXISTING)

    # create table
    cursor.execute(DATABASE_CREATE)

    base_filters = itertools.product(
            [BUILT_IN],

            ALGORITHMS,
            COMPONENTS,
            ORDERS,

            COMBO_TYPES,
            COMBO_FUNCS,
            COMBO_FUNCS,
            COMBO_FUNCS,
            COMBO_FUNCS,

            PARTITION_TYPES,
    )

    dimension_filters = list(map(lambda values: values + (1, 1), base_filters))
    dimension_filters += list(map(lambda values: values + (4, 3), base_filters))

    named_filters = map(name_filters, dimension_filters)

    for filter in named_filters:
        query = "INSERT INTO " + TABLE_FILTERS + " VALUES " + "{}".format(filter)
        cursor.execute(query)

    connection.commit()
    cursor.close()
    return 0


if __name__ == "__main__":
    main(sys.argv[1:])
