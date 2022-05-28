
from rest_framework import serializers
from server.models import *

class LocationSerializer(serializers.ModelSerializer):
    class Meta:
        model = Location
        fields = '__all__'

class CodeSerializer(serializers.ModelSerializer):
    class Meta:
        model = Code
        fields = '__all__'

class CodeLocationSerializer(serializers.ModelSerializer):
    class Meta:
        model = CodeLocation
        fields = '__all__'

class RouteSerializer(serializers.ModelSerializer):
    class Meta:
        model = Route
        fields = '__all__'

class ElementSerializer(serializers.ModelSerializer):
    class Meta:
        model = Element
        fields = '__all__'

class RegionSerializer(serializers.ModelSerializer):
    class Meta:
        model = Region
        fields = '__all__'

class ElementRegionSerializer(serializers.ModelSerializer):
    class Meta:
        model = ElementRegion
        fields = '__all__'