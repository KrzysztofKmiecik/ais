# AIS

## Business requirements
There is a business need to show ships on map by using AIS system.
![](src/main/resources/static.photos/screenshots/viking.jpeg)

## Project requirements
- [x] as a user I would like to see ships to locate them
- [x] as a user I would like to know direction and speed  of ships
- [x] as a user I would like to have possibility to mark ship as FRIEND or ENEMY 
- [x] as a user I would like to see the scale of the map 
- [x] as a user I would like to have possibility to measure a distance between severals points on the map 
- [x] as a user I would like to have stable application  thats why I need to monitor my application 
- [x] as a user I would like to get email when status FRIEND <-> ENEMY will be changed
- [x] as a user I would like to have possibility to use REST API to be able to connect via Postman or external app


# Solution

![](src/main/resources/static.photos/screenshots/aisMap.png)
![](src/main/resources/static.photos/screenshots/ais_grafana.png)
![](src/main/resources/static.photos/screenshots/ais_swagger.png)
![](src/main/resources/static.photos/screenshots/ais_docker.png)

### DONE
- [x] AIS
- [x] refresh site in cycle 65sec 
- [x] ruler for distance calculation  (plugin was added)
- [x] map's scale is presented in left-bottom corner (plugin was added)
- [x] ship's status change (icon and info are changed)-> FRIEND(click),ENEMY(dbclick)
- [x] circle around corresponds to weather visibility (due to the limits(50requests/day) from stormAPI the random  values were generated )
- [x] direction and speed  of movement is presented by the polygon
- [x] email  -> "Ship MUNKEN was updated"
- [x] Profiles (dev,prod)
- [x] Inputs validation
- [x] unit Test
- [x] REST API with Swagger UI
- [x] log history
- [x] healthChecks metrics, prometheus, Grafana
- [x] Docker-compose 

### TO DO
- [ ] Security -> CAPTAIN, SAILOR
- [ ] Locale i18n
- [ ] unit Tests -> ongoing
- [ ] integration test

### Used Technologies
- SpringBoot
- Postgres
- LeafLet
- Docker
- Thymeleaf with Bootstrap
- Swagger UI
- Grafana
- Docker-Compose

## Info


### links
- app    -> http://localhost:8080
- API    -> http://localhost:8080/swagger-ui.html
- metrics-> http://localhost:8080/actuator
- prometheus -> http://localhost:9090
- http://localhost:3000 -> grafana (login:admin pass:admin) ; url http://prometheus:9090; import jvm Micrometer ID 4701



### Reference Documentation

Tytu?? projektu, oraz czemu s??u??y Twoje rozwi??zanie
Jak uruchomi?? Twoj?? aplikacj??
Screeny i/lub film prezentuj??cy Twoj?? rozwi??zanie w dzia??aniu

Prac?? konkursow?? prze??lij w nast??puj??cym formularzu do 5 grudnia 2021 do godziny 23:59: https://forms.gle/VCqeLHL4mF6UrECP9

4

. Zadanie konkursowe:

Wykorzystuj??c dane pobierane z AIS utw??rz aplikacj??, kt??ra b??dzie wy??wietla??a informacje na temat jednostek morskich na mapie.

Co dok??adnie i w jakim celu ma to realizowa??? ??? To Twoja inwencja, poka?? Nam jaki, ciekawy projekt mo??na zrealizowa??.

Mo??e by?? to aplikacja do ??ledzenia jednostek w czasie rzeczywistym, projekt gdzie wcielasz si?? w jednostek na mapie i swobodnie ni?? sterujesz, lub dowolny inny projekt! Chcemy zobaczy?? Tw??j pomys?? ????

Do swojej implementacji koniecznie wykorzystaj:

Hibernate
PostgreSQL
Docker
Ale r??wnie?? mo??esz doda?? dowoln?? wybran?? przez Ciebie technologie ??? bez ogranicze??, liczy si?? inwencja ????

Mo??esz w pe??ni wykorzysta?? kod ??r??d??owy opracowany w trakcie LiveStrema w ramach, kt??rego og??oszony by?? konkurs i dowolnie go rozbudowa??.


* Do pobierania informacji na temat lokalizacji stat??w:
* * https://www.barentswatch.no/en/about/open-data-via-barentswatch/
* * https://www.barentswatch.no/minside/




  