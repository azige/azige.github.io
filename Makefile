all: resource

resource: Resource.properties

Resource.properties:
	native2ascii Resource.txt > Resource.properties

clean:
	rm Resource.properties
