#
#   A script to generate the filters.sqlite database.
#

import itertools
import sqlite3
import sys

TABLE_FILTERS = "filters"
COLUMN_NAME = "name"
COLUMN_COMPONENT = "component"
COLUMN_ORDERING = "ordering"
COLUMN_ZIP_METHOD = "zip_method"
COLUMN_BASE_OP = "base_op"
COLUMN_NUM_ROWS = "num_rows"
COLUMN_NUM_COLS = "num_cols"
COLUMN_IS_DEFAULT = "is_default"

RED = 0
GREEN = 1
BLUE = 2
HUE = 3
SATURATION = 4
VALUE = 5

DESCENDING = 0
ASCENDING = 1

MOVE_PIXELS = 0
MOVE_COMPONENT = 1
MOVE_RED = 2
MOVE_GREEN = 3
MOVE_BLUE = 4
MOVE_HUE = 5
MOVE_SATURATION = 6
MOVE_VALUE = 7

SORT = 0
HEAPIFY = 1

COMPONENTS = [RED, HUE, SATURATION]
ORDERS = [ASCENDING, DESCENDING]
ZIP_METHODS = [MOVE_PIXELS, MOVE_RED, MOVE_GREEN, MOVE_SATURATION, MOVE_HUE]
SORT_TYPES = [SORT, HEAPIFY]
DIMENSIONS = [1, 4, 16]


def named_values(values):
    name = "".join((str(val) for val in values))
    return tuple([name] + list(values))


def main(argv):

    sql_delete_existing = "DROP TABLE IF EXISTS " + TABLE_FILTERS

    # sql statement to create the database
    sql_db_create = "CREATE TABLE " + TABLE_FILTERS              \
            + " (" + COLUMN_NAME + " TEXT PRIMARY KEY NOT NULL, "  \
            + COLUMN_COMPONENT + " INTEGER NOT NULL, " \
                    + COLUMN_ORDERING + " INTEGER NOT NULL, "                \
            + COLUMN_ZIP_METHOD + " INTEGER NOT NULL, "           \
            + COLUMN_BASE_OP + " INTEGER NOT NULL, "              \
            + COLUMN_NUM_ROWS + " INTEGER NOT NULL, "             \
            + COLUMN_NUM_COLS + " INTEGER NOT NULL,"             \
            + COLUMN_IS_DEFAULT + " INTEGER NOT NULL); "          \

    # open database
    connection = sqlite3.connect(argv[0])
    cursor = connection.cursor()

    # delete any existing entries
    cursor.execute(sql_delete_existing)

    # create table
    cursor.execute(sql_db_create)

    for values in map(named_values,
                      itertools.product(COMPONENTS, ORDERS, ZIP_METHODS, SORT_TYPES, DIMENSIONS, DIMENSIONS, [1])):
        query = "INSERT INTO " + TABLE_FILTERS + " VALUES " + "{}".format(values)
        cursor.execute(query)

    connection.commit()
    cursor.close()
    return 0


if __name__ == "__main__":
    main(sys.argv[1:])
