from django.db import models

class Location(models.Model):
    locationName = models.CharField(max_length = 150)

    def _str_(self):
        return self.locationName

class Code(models.Model):
    code = models.CharField(primary_key = True, max_length = 10)

    def _str_(self):
        return self.code

class CodeLocation(models.Model):
    code = models.ForeignKey(Code, on_delete = models.CASCADE)
    location = models.ForeignKey(Location,on_delete = models.CASCADE)

    def _str_(self):
        return self.location + " " + self.code

class Route(models.Model):
    code1 = models.ForeignKey(CodeLocation, on_delete = models.CASCADE,related_name = "code_fk")
    code2 = models.ForeignKey(CodeLocation, on_delete = models.CASCADE,related_name="nextcode_fk")
    distance = models.IntegerField()
    aid_message = models.CharField( max_length = 150)

    def _str_(self):
        return self.code + " " + self.nextCode + " " + self.location + " " + str(self.distance) + " " + self.aid_message

class Element(models.Model):
    name = models.CharField(max_length = 50)

    def _str_(self):
        return self.name

class Region(models.Model):
    codeLocation = models.ForeignKey(CodeLocation,on_delete = models.CASCADE, null = True )
    name = models.CharField(max_length = 50) 

    def _str_(self):
        return self.name + " " + str(self.codeLocation)

class ElementRegion(models.Model):
    region = models.ForeignKey(Region,on_delete = models.CASCADE)
    element = models.ForeignKey(Element,on_delete = models.CASCADE)

    def _str_(self):
        return str(self.region) + " " + str(self.element)