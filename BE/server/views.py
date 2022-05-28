from django.shortcuts import render
from rest_framework import viewsets
from rest_framework.authtoken.models import Token
from rest_framework.response import Response
from rest_framework import status
from rest_framework.decorators import api_view, action
from django.views.decorators.csrf import csrf_exempt
from django.contrib.auth.models import User
from django.contrib.auth import authenticate
from django.http import HttpResponse
from django.http.response import JsonResponse
from rest_framework.parsers import JSONParser
from rest_framework.exceptions import APIException
from django.contrib.auth.hashers import check_password
from django.contrib.auth import authenticate
from .serializers import *
from django.contrib.auth import get_user_model
import json
from django.shortcuts import get_object_or_404
import datetime, calendar
from datetime import date
from server import utils



@api_view(['POST'])
def test1(request):
    if request.method == 'POST':
        try:
            data = json.loads(request.body.decode('utf-8'))

            return Response({'test':"Se afiseaza " + str(data["mesaj"])})
        except Exception as er:
            return Response({'error':str(er)},status=status.HTTP_400_BAD_REQUEST)


@api_view(['GET'])
def getCodes(request):
    try:
        data = request.headers
        codeLocation = CodeLocation.objects.filter(location = data["location"])
        serializer = CodeLocationSerializer(codeLocation,many=True)
        return Response(serializer.data)
    except Exception as er:
        return Response({'error':str(er)},status=status.HTTP_400_BAD_REQUEST)

@api_view(['GET'])
def getRegion(request):
    try:
        data = request.headers
        list_regions = utils.getRegion(data["location"])
        return Response(list_regions)
    except Exception as er:
        return Response({'error':str(er)},status=status.HTTP_400_BAD_REQUEST)
        
@api_view(['GET'])
def getElementRegion(request):
    try:
        data = request.headers
        list_element_regions = utils.getElementRegion(data["location"])
        
        return Response(list_element_regions)
    except Exception as er:
        return Response({'error':str(er)},status=status.HTTP_400_BAD_REQUEST)

@api_view(['GET'])
def getElements(request):
    try:
        data = request.headers
        
        list_element = utils.getElements(data["location"])
        
        return Response(list_element)
    except Exception as er:
        return Response({'error':str(er)},status=status.HTTP_400_BAD_REQUEST)


@api_view(['GET'])
def getRoutes(request):
    try:
        list_routes = []
        data=request.headers
        codesLocation = CodeLocation.objects.filter(location = data["location"] )
        print("Before route")
        for code in codesLocation:
            routes = Route.objects.raw("Select * from server_route where server_route.code1_id = " + str(code.id))
            serializerRoute = RouteSerializer(routes, many = True)
            list_routes = list_routes + serializerRoute.data
        
        return Response(list_routes)
    except Exception as er:
        return Response({'error':str(er)},status=status.HTTP_400_BAD_REQUEST)

@api_view(['GET'])
def getInfoLocation(request):
    try:
        data=request.headers
        location=Location.objects.raw("Select * from server_location where id="+str(data["idLocation"]))

        serializer = LocationSerializer(location,many=True)
        if len(serializer.data) == 1:   
            return Response({"mesaj":"Locatie existenta"})
        else:
            return Response({"mesaj":"Locatia nu exista"})
    except Exception as er:
        return Response({'error':str(er)},status=status.HTTP_400_BAD_REQUEST)