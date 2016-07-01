#
#   A script to generate the filters.sqlite database.
#

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

CMPNT_ALPHA = 0
CMPNT_RED = 1
CMPNT_GREEN = 2
CMPNT_BLUE = 3
CMPNT_HUE = 4
CMPNT_SAT = 5
CMPNT_VAL = 6

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

# Flag indicating this filter is built-in
BUILT_IN = 1

FILTERS = (
    (
        "Awesome X",
        BUILT_IN,

        SORT,
        CMPNT_SAT,
        DESCENDING,

        COMBINE_ARGB,
        PRESERVE,
        PRESERVE,
        REPLACE,
        PRESERVE,

        GRID_PARTITION,
        1000000,
        16
    ),

    (
        "Simon",
        BUILT_IN,

        SORT,
        CMPNT_SAT,
        DESCENDING,

        COMBINE_ARGB,
        PRESERVE,
        PRESERVE,
        REPLACE,
        PRESERVE,

        GRID_PARTITION,
        16,
        1000000
    ),

    (
        "Stan",
        BUILT_IN,

        SORT,
        CMPNT_VAL,
        DESCENDING,

        COMBINE_ARGB,
        REPLACE,
        REPLACE,
        REPLACE,
        REPLACE,

        GRID_PARTITION,
        1000000,
        48
    ),

    (
        "Killface",
        BUILT_IN,

        HEAPIFY,
        CMPNT_VAL,
        DESCENDING,

        COMBINE_ARGB,
        REPLACE,
        REPLACE,
        REPLACE,
        REPLACE,

        GRID_PARTITION,
        16,
        12
    ),

    (
        "Wendell",
        BUILT_IN,

        HEAPIFY,
        CMPNT_VAL,
        DESCENDING,

        COMBINE_AHSV,
        PRESERVE,
        PRESERVE,
        REPLACE,
        REPLACE,

        GRID_PARTITION,
        4,
        3
    ),

    (
        "Wendell X",
        BUILT_IN,

        HEAPIFY,
        CMPNT_SAT,
        DESCENDING,

        COMBINE_ARGB,
        PRESERVE,
        PRESERVE,
        REPLACE,
        PRESERVE,

        GRID_PARTITION,
        4,
        3
    ),

    (
        "Hooper",
        BUILT_IN,

        SORT,
        CMPNT_VAL,
        ASCENDING,

        COMBINE_ARGB,
        PRESERVE,
        PRESERVE,
        REPLACE,
        REPLACE,

        GRID_PARTITION,
        1,
        1
    ),

    (
        "Sinn",
        BUILT_IN,

        SORT,
        CMPNT_VAL,
        ASCENDING,

        COMBINE_ARGB,
        PRESERVE,
        REPLACE,
        REPLACE,
        PRESERVE,

        GRID_PARTITION,
        1,
        1
    ),

    (
        "Cody",
        BUILT_IN,

        HEAPIFY,
        CMPNT_RED,
        DESCENDING,

        COMBINE_ARGB,
        PRESERVE,
        REPLACE,
        SUBTRACT,
        MULTIPLY,

        GRID_PARTITION,
        1,
        1
    ),

    (
        "Cody 2",
        BUILT_IN,

        SORT,
        CMPNT_HUE,
        DESCENDING,

        COMBINE_AHSV,
        PRESERVE,
        PRESERVE,
        MULTIPLY,
        SUBTRACT,

        GRID_PARTITION,
        1000000,
        1
    ),

    (
        "Nearl",
        BUILT_IN,

        SORT,
        CMPNT_GREEN,
        ASCENDING,

        COMBINE_ARGB,
        REPLACE,
        REPLACE,
        REPLACE,
        REPLACE,

        GRID_PARTITION,
        1,
        1000000
    ),

    (
        "Master Cylinder",
        BUILT_IN,

        SORT,
        CMPNT_VAL,
        DESCENDING,

        COMBINE_ARGB,
        REPLACE,
        REPLACE,
        PRESERVE,
        REPLACE,

        GRID_PARTITION,
        1000000,
        1
    ),

    (
        "Antagone",
        BUILT_IN,

        SORT,
        CMPNT_HUE,
        DESCENDING,

        COMBINE_ARGB,
        PRESERVE,
        REPLACE,
        XOR,
        PRESERVE,

        GRID_PARTITION,
        64,
        48
    ),

    (
        "Grace",
        BUILT_IN,

        SORT,
        CMPNT_HUE,
        DESCENDING,

        COMBINE_ARGB,
        PRESERVE,
        XOR,
        PRESERVE,
        REPLACE,

        GRID_PARTITION,
        64,
        48
    ),

    (
        "Darcell",
        BUILT_IN,

        SORT,
        CMPNT_HUE,
        DESCENDING,

        COMBINE_ARGB,
        PRESERVE,
        REPLACE,
        PRESERVE,
        XOR,

        GRID_PARTITION,
        64,
        48
    ),

    (
        "Dread Lobster",
        BUILT_IN,

        BST,
        CMPNT_SAT,
        DESCENDING,

        COMBINE_AHSV,
        PRESERVE,
        PRESERVE,
        MULTIPLY,
        PRESERVE,

        GRID_PARTITION,
        32,
        24
    ),

    (
        "Xander",
        BUILT_IN,

        SORT,
        CMPNT_SAT,
        DESCENDING,

        COMBINE_ARGB,
        REPLACE,
        REPLACE,
        REPLACE,
        REPLACE,

        GRID_PARTITION,
        1000000,
        1
    )
)

def main(argv):

    # open database
    connection = sqlite3.connect(argv[0])
    cursor = connection.cursor()

    # delete any existing entries
    cursor.execute(DATABASE_DELETE_EXISTING)

    # create table
    cursor.execute(DATABASE_CREATE)

    for filter in FILTERS:
        query = "INSERT INTO " + TABLE_FILTERS + " VALUES " + "{}".format(filter)
        cursor.execute(query)

    connection.commit()
    cursor.close()
    return 0


if __name__ == "__main__":
    main(sys.argv[1:])
