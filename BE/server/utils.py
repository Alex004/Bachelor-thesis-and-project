from .serializers import *


def getRegion(id):
    list_regions = []
    codesLocation = CodeLocation.objects.filter(location = id)
    for code in codesLocation:
        regions = Region.objects.filter(codeLocation = str(code.id))
        serializerRegion = RegionSerializer(regions, many = True)
        list_regions = list_regions + serializerRegion.data

    return list_regions

def getElementRegion(id):
    list_element_regions = []
    list_regions = getRegion(id)
    for region in list_regions:
        elementsRegion = ElementRegion.objects.filter(region = region["id"])
        serializerElementRegion = ElementRegionSerializer(elementsRegion, many = True)
        list_element_regions = list_element_regions + serializerElementRegion.data

    return list_element_regions

def getElements(id):
    list_element = []
    list_element_regions = getElementRegion(id)
    
    for element in list_element_regions:
        elementClass = Element.objects.filter(id = element["element"])
        serializerElement = ElementSerializer(elementClass, many = True)
        list_element = list_element + serializerElement.data
    print(list_element)
    return list_element