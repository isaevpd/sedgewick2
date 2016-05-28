from PIL import Image

image = Image.open("6x5.png")

print image.load()[2, 2]