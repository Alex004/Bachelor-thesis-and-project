import os
os.environ.setdefault("DJANGO_SETTINGS_MODULE", "licenta.settings")
import django
django.setup()
from server.models import *
from server.serializers import *




def completeCode():
    code = ""
    list_err = []
    for i in range(ord('P') - ord('A')+1):
        for j in range(ord('P') - ord('A')+1):
            for k in range(ord('P') - ord('A')+1):
                try:
                    code = str(chr(i+ord('A'))) + str(chr(j+ord('A'))) + str(chr(k+ord('A'))) 
                    codeClass = Code(code = code)
                    codeClass.save()
                except Exception as er:
                    print(er)
                    list_err.append(code)

    print("Error elements Code: ")
    print(list_err)


def completeLocation():
    location = 'location'
    list_err=[]
    for i in range (50):
        try:
            locationName = location + str(i) 
            locationClass = Location(locationName = locationName)
            locationClass.save()
        except Exception as er:
            print(er)
            list_err.append(i)

    print("Error elements location: ")
    print(list_err)

def completeCodeLocation():
    list_elem_code = ["AAA","AAB","AAC","AAD","AAE","AAF","AAG"]
    location = "location0"
    list_err = []
    locationClass = Location.objects.raw("Select * from server_location where locationName='location0'")
    # for i in range(len(matrix_way)):
    #     for j in range(len(matrix_way[0])):
    #         try:
    #         codeClassCurrent = Code.objects.raw("Select * from server_code where code="+matrix_way)
    #         locationClass = CodeLocation(locationName = locationName)
    #         locationClass.save()
    #     except Exception as er:
    #         print(er)
    #         list_err.append([i,j])
    for code in list_elem_code:
        
        try:
            # codeClass = Code.objects.raw("Select * from server_code where code=" + code)
            # print(codeClass)
            codeLocation = CodeLocation()
            codeLocation.code = Code.objects.get(code = code)
            codeLocation.location = Location.objects.get(locationName = location)
            codeLocation.save()
        except Exception as er:
            print(er)
            list_err.append(code)
    print("Error elements codelocation: ")
    print(list_err)

def completeRoute():
    list_elem_code = ["AAA","AAB","AAC","AAD","AAE","AAF","AAG"]
    location = "location0"
    # matrix_way = [[0,1,1,1,0,0,0,0,0],[1,0,1,0,0,0,0,0,1],[1,1,0,1,0,0,0,0,0],[1,0,1,0,1,0,1,1,0],[0,0,0,1,0,1,1,0,0],[0,0,0,0,1,0,0,0,0],[0,0,0,1,0,0,1,0,0],[0,0,0,1,0,0,1,0,0],[0,1,0,0,0,0,0,0,0]]
    matrix_way = [[0,1,0,0,1,1,0],[1,0,1,0,1,1,0],[0,1,0,1,1,0,0],[0,0,1,0,1,0,0],[1,1,1,1,0,1,0],[1,1,0,0,1,0,1],[0,0,0,0,0,1,0]]
    matrix_distance = [[0,2,0,0,1,1,0],[2,0,1,0,4,3,0],[0,1,0,1,5,0,0],[0,0,1,0,6,0,0],[1,4,5,6,0,1,0],[1,1,0,0,1,0,4],[0,0,0,0,0,4,0]]
    list_err = []
    location = Location.objects.get(locationName = location)
    print("Locatia " , location)
    for i in range(len(matrix_way)):
        for j in range(len(matrix_way[0])):
            try:
                if matrix_way[i][j] == 1:
                
                    route = Route(distance = matrix_distance[i][j])
                    code = Code.objects.get(code = list_elem_code[i])
                    nextCode = Code.objects.get(code = list_elem_code[j])
                    print(code)
                    print(nextCode)
                    
                    codeLocationCode = CodeLocation.objects.get(code = code, location = location)
                    print("Code ", codeLocationCode)
                    codeLocationCodeNext = CodeLocation.objects.get(code = nextCode, location = location)
                    print("Code ", codeLocationCodeNext)
                   
                    route.code1 = codeLocationCode
                    route.code2 = codeLocationCodeNext
                    print("Route ", route)
                    route.save()
            except Exception as er:
                print(er)
                list_err.append([i,j])
    print("Error elements route: ")
    print(list_err)

def setAidMessageForALocation():
    list_condition_query = ""
    codesLocation = CodeLocation.objects.filter(location = 1 )
    list_aid = ['Mergeti inainte', 'Mergeti la stanga', 'Mergeti la stanga apoi iar la stanga', 'Mergeti inainte', 'Mergeti inainte', 'Mergeti la dreapta\n', 'Mergeti la dreapta apoi la stanga', 'Mergeti inainte', 'Mergeti inainte\n', 'Mergeti inainte apoi mergeti la dreapta', 'Mergeti inainte', 'Mergeti inainte apoi mergeti la dreapta', 'Mergeti inainte', 'Mergeti inainte apoi mergeti la stanga', 'Mergeti inainte apoi mergeti la stanga', 'Mergeti inainte apoi mergeti la stanga', 'Mergeti la dreapta', 'Mergeti la dreapta apoi iar la dreapta', 'Mergeti la dreapta', 'Mergeti la stanga', 'Mergeti inainte apoi la dreapta', 'Mergeti inainte apoi la stanga']
    for codeLocation in codesLocation:
        if list_condition_query == "":
            list_condition_query = "code1_id = " + str(codeLocation.id)
        else:
            list_condition_query = list_condition_query + " or code1_id = " + str(codeLocation.id)
    routes = Route.objects.raw("Select * from server_route where " + list_condition_query)
    serializer = RouteSerializer(routes, many = True)
    for route in serializer.data:
        Route.objects.filter(pk = route["id"]).update(aid_message = list_aid[int(route["id"]-1)])





def completeElement():
    list_element = []
    list_element.append("person");
    list_element.append("bicycle");
    list_element.append("car");
    list_element.append("motorcycle");
    list_element.append("airplane");
    list_element.append("bus");
    list_element.append("train");
    list_element.append("truck");
    list_element.append("boat");
    list_element.append("traffic light");
    list_element.append("fire hydrant");
    list_element.append("stop sign");
    list_element.append("parking meter");
    list_element.append("bench");
    list_element.append("bird");
    list_element.append("cat");
    list_element.append("dog");
    list_element.append("horse");
    list_element.append("sheep");
    list_element.append("cow");
    list_element.append("elephant");
    list_element.append("bear");
    list_element.append("zebra");
    list_element.append("giraffe");
    list_element.append("backpack");
    list_element.append("umbrella");
    list_element.append("handbag");
    list_element.append("tie");
    list_element.append("suitcase");
    list_element.append("frisbee");
    list_element.append("skis");
    list_element.append("snowboard");
    list_element.append("sports ball");
    list_element.append("kite");
    list_element.append("baseball bat");
    list_element.append("baseball glove");
    list_element.append("skateboard");
    list_element.append("surfboard");
    list_element.append("tennis racket");
    list_element.append("bottle");
    list_element.append("wine glass");
    list_element.append("cup");
    list_element.append("fork");
    list_element.append("knife");
    list_element.append("spoon");
    list_element.append("bowl");
    list_element.append("banana");
    list_element.append("apple");
    list_element.append("sandwich");
    list_element.append("orange");
    list_element.append("broccoli");
    list_element.append("carrot");
    list_element.append("hot dog");
    list_element.append("pizza");
    list_element.append("donut");
    list_element.append("cake");
    list_element.append("chair");
    list_element.append("couch");
    list_element.append("potted plant");
    list_element.append("bed");
    list_element.append("dining table");
    list_element.append("toilet");
    list_element.append("tv");
    list_element.append("laptop");
    list_element.append("mouse");
    list_element.append("remote");
    list_element.append("keyboard");
    list_element.append("cell phone");
    list_element.append("microwave");
    list_element.append("oven");
    list_element.append("toaster");
    list_element.append("sink");
    list_element.append("refrigerator");
    list_element.append("book");
    list_element.append("clock");
    list_element.append("vase");
    list_element.append("scissors");
    list_element.append("teddy bear");
    list_element.append("hair drier");
    list_element.append("toothbrush");

    list_err = []
    for element in list_element:
        try:
            
            elementClass = Element(name = element)
            elementClass.save()
        except Exception as er:
            print(er)
            list_err.append(element)
    print("Error elements in completeElement: ")
    print(list_err)

def completeRegion():
    list_err = []
    list_region = [["Bucatarie",1],["Sufragerie",2],["Balcon",3],["Dormitor",4]]
    for region in list_region:
        try:
            # nextCode = Code.objects.get(code = list_elem_code[j])
            codeLocation = CodeLocation.objects.get(id = str(region[1]))
            print(codeLocation)
            # serializerCodeLocation = CodeLocationSerializer(codeLocation, many=True)
            # print(serializerCodeLocation.data[0])
            print("\n\n\n")
            regionClass = Region(name = region[0], codeLocation = codeLocation)
            regionClass.save()
        except Exception as er:
            print(er)
            list_err.append(element)
    print("Error elements in completeRegion: ")
    print(list_err)

def completeRegionElement():
    list_err = []
    list_region = [["Bucatarie",1],["Sufragerie",2],["Balcon",3],["Dormitor",4]]
    list_elements_in_region = [["cup","knife","fork","spoon","banana","refrigerator"],["tv","suitcase","chair","couch"],["potted plant"],["bed","book","backpack"]]
    for regionPoz in range(len(list_region)):
        for elementPoz in range(len(list_elements_in_region[regionPoz])):
            try:
                
                element = Element.objects.get(name = str(list_elements_in_region[regionPoz][elementPoz]))
                # serializerElements = ElementSerializer(element)
                
                region = Region.objects.get(name = str(list_region[regionPoz][0]) , codeLocation_id = str(list_region[regionPoz][1]))
                # serializerRegion = RegionSerializer(region)

                elementRegionClass = ElementRegion(region = region, element = element)
                elementRegionClass.save()
            except Exception as er:
                print(er)
                list_err.append([list_region[region],element])
    print("Error elements in completeRegionElement: ")
    print(list_err)


# completeCode()
# completeLocation()
# completeCodeLocation()
# completeRoute()
setAidMessageForALocation()
completeElement()
completeRegion()
completeRegionElement()







