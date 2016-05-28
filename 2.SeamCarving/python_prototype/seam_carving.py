from PIL import Image
import math
import numpy as np

class SeamCarver(object):
    def __init__(self, picture):
        self.width, self.height = picture.size

        self.picture_matrix = np.array([[picture.getpixel((x, y)) for x in range(self.width)] for y in range(self.height)], dtype=np.int16)
        
        self.energy_matrix = np.array([[self.energy(i, j) for j in range(self.width)] for i in range(self.height)], dtype=np.float16)

        self.cumulated_matrix = np.array([[float('inf') for y in range(self.width)] for x in range(self.height)], dtype=np.float32)

        self.parent_matrix = np.array([[0 for y in range(self.width)] for x in range(self.height)], dtype=np.uint16)

        self.is_tranposed = False

        self.get_shortest_distances()

    # HELPER FUNCTIONS
    def get_delta_x(self, x, y):
        '''helper for energy function'''
        top_pixel = self.picture_matrix[x-1][y]
        bottom_pixel = self.picture_matrix[x+1][y]

        return sum(map(lambda x, y: (x - y) ** 2, bottom_pixel, top_pixel))

    def get_delta_y(self, x, y):
        '''helper for energy function'''
        # print self.picture_matrix.shape
        # print x, y
        # print self.energy_matrix\
        left_pixel = self.picture_matrix[x][y-1]
        right_pixel = self.picture_matrix[x][y+1]
        return sum(map(lambda x, y: (x - y) ** 2, left_pixel, right_pixel))


    def get_best_parent(self, x, y):
        # check pixel directly below the argument indices
        mid_child = self.energy_matrix[x+1][y]
        # store current value as it gets compared a lot
        current_cumulated_value = self.cumulated_matrix[x][y]
        sum_below = mid_child + current_cumulated_value

        if sum_below < self.cumulated_matrix[x+1][y]:
            # print 'choosing', mid_child
            self.cumulated_matrix[x+1][y] = sum_below
            self.parent_matrix[x+1][y] = y

        if y != 0:
            left_child = self.energy_matrix[x+1][y-1]
            # print left_child
            sum_left_below = left_child + current_cumulated_value
            if sum_left_below < self.cumulated_matrix[x+1][y-1]:
                # print 'choosing', left_child
                self.cumulated_matrix[x+1][y-1] = sum_left_below
                self.parent_matrix[x+1][y-1] = y

        if y != (self.energy_matrix.shape[1] - 1):
            right_child = self.energy_matrix[x+1][y+1]
            # print right_child
            sum_right_below = right_child + current_cumulated_value
            if sum_right_below < self.cumulated_matrix[x+1][y+1]:
                # print 'choosing', right_child
                self.cumulated_matrix[x+1][y+1] = sum_right_below
                self.parent_matrix[x+1][y+1] = y


    def get_shortest_distances(self):
        '''fills parent and cumulated matrices'''
        height, width = self.cumulated_matrix.shape

        for i in range(height):
            for j in range(width):
                self.cumulated_matrix[i][j] = float('inf')

        for j in range(width):
            self.cumulated_matrix[0][j] = 1000

        for i in range(height-1):
            for j in range(width):
                self.get_best_parent(i, j)

    def get_seam(self):
        last_row_index = len(self.energy_matrix) - 1
        seam = [0 for _ in range(last_row_index + 1)]

        # tuple with index, value of the column with minimum value at bottom row to start our seam
        parent = min(enumerate(self.cumulated_matrix[last_row_index]), key=lambda x: x[1])[0]
        seam[last_row_index] = parent
        # print "seam is", seam

        # tricky loop with parent update on each turn
        for i in range(last_row_index, 0, -1):
            parent = self.parent_matrix[i][parent]
            seam[i-1] = parent

        return seam

    def transpose(self):
        self.picture_matrix = np.transpose(self.picture_matrix)
        self.energy_matrix = np.transpose(self.energy_matrix)
        self.cumulated_matrix = np.transpose(self.cumulated_matrix)
        self.parent_matrix = np.transpose(self.parent_matrix)

        self.height, self.width = self.energy_matrix.shape

        self.get_shortest_distances()

        if self.is_tranposed:
            self.is_tranposed = False
        else:
            self.is_tranposed = True

    def remove_seam_copy_helper(self, array, seam, picture=False):
        '''
        helper function for remove_vertical_seam
        or remove_horizontal_seam
        removes a seam from an MxN array
        returning a new array which is MxN - 1
        '''
        if picture:
            x, y, z = array.shape
            new_array = np.zeros(shape=(x, y-1, z), dtype=array.dtype)
            for i in range(x):
                # print array
                new_row = np.delete(array[i], seam[i], axis=0)
                new_array[i] = new_row

            return new_array

        x, y = array.shape
        new_array = np.zeros(shape=(x, y-1), dtype=array.dtype)
        for i in range(x):
            new_row = np.delete(array[i], seam[i], axis=0)
            new_array[i] = new_row

        return new_array

    def remove_seam_wrapper(self, seam):
        self.picture_matrix = self.remove_seam_copy_helper(self.picture_matrix, seam, picture=True)
        self.energy_matrix = self.remove_seam_copy_helper(self.energy_matrix, seam)
        self.cumulated_matrix = self.remove_seam_copy_helper(self.cumulated_matrix, seam)
        self.parent_matrix = self.remove_seam_copy_helper(self.parent_matrix, seam)

        x, y = self.energy_matrix.shape
        # recompute left and right pixel from the removed seam
        # leave borders unchanged
        for i in range(1, x - 1):
            j = seam[i]
            if j < y - 1:
                self.energy_matrix[i][j] = self.energy(i, j)
            if j > 1:
                self.energy_matrix[i][j-1] = self.energy(i, j-1)


    # ACTUAL API
    def picture(self):
        if self.is_tranposed:
            self.transpose()

        image = Image.fromarray(self.picture_matrix, 'RGB')
        return image

    def energy(self, x, y):
        assert x >= 0 and y >= 0, 'One of the indices is less than 0'
        assert x < self.height and y < self.width, 'Out of bounds'
        if (x == self.height - 1 or y == self.width - 1) or (x == 0 or y == 0):
            return 1000;

        delta_x = self.get_delta_x(x, y)
        delta_y = self.get_delta_y(x, y)
        return round(math.sqrt(delta_x + delta_y), 2)

    def find_horizontal_seam(self):
        if self.is_tranposed:
            return self.get_seam()
        self.transpose()
        return self.get_seam()

    def find_vertical_seam(self):
        if not self.is_tranposed:
            return self.get_seam()
        self.transpose()
        return self.get_seam()

    def remove_horizontal_seam(self):
        seam = self.find_horizontal_seam()
        self.remove_seam_wrapper(seam)
        self.get_shortest_distances()

    def remove_vertical_seam(self):
        seam = self.find_vertical_seam()
        self.remove_seam_wrapper(seam)
        self.get_shortest_distances()







# picture_3x4 = Image.open('3x4.png')

# sc_object = SeamCarver(picture_3x4)

# print sc_object.height
# print sc_object.width


def print_stuff(sc_object):
    print "Height is {}, Width is {}".format(sc_object.height, sc_object.width)
    energy_matrix = sc_object.energy_matrix
    print "Energy matrix is:"
    for row in energy_matrix:
        print row

    cumulated_matrix = sc_object.cumulated_matrix
    print "Cumulated matrix is:"
    for row in cumulated_matrix:
        print row

    parent_matrix = sc_object.parent_matrix
    print "Parent matrix is:"
    for row in parent_matrix:
        print row

    print 'Vertical seam is:'
    print sc_object.find_vertical_seam()

    print 'Horizontal seam is:'
    print sc_object.find_horizontal_seam()


def test(picture):
    sc_object = SeamCarver(picture)
    print_stuff(sc_object)
    # picture_matrix = sc_object.picture_matrix
    # print "Picture matrix is:"
    # # print picture_matrix
    # # for row in picture_matrix:
    # #     print [list(x) for x in row]

    # print "Height is {}, Width is {}".format(sc_object.height, sc_object.width)
    # energy_matrix = sc_object.energy_matrix
    # print "Energy matrix is:"
    # for row in energy_matrix:
    #     print row
    # print 'Horizontal seam is:'
    # print sc_object.find_horizontal_seam()
    # cumulated_matrix = sc_object.cumulated_matrix
    # print "Cumulated matrix is:"
    # for row in cumulated_matrix:
    #     print row

    # parent_matrix = sc_object.parent_matrix
    # print "Parent matrix is:"
    # for row in parent_matrix:
    #     print row
    # sc_object.remove_vertical_seam()
    # sc_object.remove_vertical_seam()
    # sc_object.remove_vertical_seam()
    # sc_object.remove_horizontal_seam()
    # sc_object.remove_horizontal_seam()
    # print sc_object.picture_matrix
    # sc_object.picture().show()
    # sc_object.transpose()
    # for i in range(15):
    #     sc_object.remove_vertical_seam()
    # sc_object.picture().save('my_pic.png')
    # print sc_object.picture_matrix
    # sc_object.remove_vertical_seam()
    # sc_object
    # print_stuff(sc_object)
    # print 'Vertical seam is:'
    # print sc_object.find_vertical_seam()
    # print 'Horizontal seam is:'
    # print sc_object.find_horizontal_seam()
    # sc_object.picture().show()
    # for i in range(20):
    # sc_object.remove_vertical_seam()
    # sc_object.picture().show()
    # print_stuff(sc_object)
    # print_stuff(sc_object)
    # print sc_object.remove_seam_copy_helper(energy_matrix)
    # print sc_object.is_tranposed
    # print sc_object.find_horizontal_seam()
    # # sc_object.transpose()
    # print sc_object.is_tranposed
    # sc_object.picture().show()

    # print sc_object.is_tranposed
    # print sc_object.find_vertical_seam()
    # print sc_object.is_tranposed
    # print sc_object.is_tranposed

    # print sc_object.parent_matrix
    # sc_object.transpose()
    # energy_matrix = sc_object.energy_matrix
    # print "Energy matrix is:"
    # for row in energy_matrix:
    #     print row
    # picture = sc_object.picture()
    # picture.show()


test(Image.open('6x5.png'))

# test(Image.open('7x10.png'))
# test(Image.open('4x6.png'))
# test(Image.open('7x10.png'))





