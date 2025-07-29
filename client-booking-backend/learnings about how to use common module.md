common module can be done in 2 ways apparently for now in dev environment.
1. using a parent module style
2. not using a dedicated parent module, but using the spring boot parent

example of parent module style:
https://github.com/alexmarqs/springboot-multimodule-example
so basically you need to looks like build from parent module maybe. and have the common module in the parent module pom xml.
and all the libraries that are common like testing, secrutiy keep them in the parent pom, and keep the child modules thin.

2nd of using common module is using spring boot parent:
https://www.baeldung.com/spring-boot-multiple-modules
using the spring boot starter parent. it will let spring boot figure out the local repository common modules.
u dont need a parent pom.xml.


but whatever way you are using, in the common module pom.xml never have the spring boot maven plugin
```xml

<build>
		<plugins>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-compiler-plugin</artifactId>
				<configuration>
					<annotationProcessorPaths>
						<path>
							<groupId>org.projectlombok</groupId>
							<artifactId>lombok</artifactId>
							<version>1.18.30</version>
						</path>
					</annotationProcessorPaths>
				</configuration>
			</plugin>
<!--          *******************  DONT USE THE BELOW THING IN A COMMON MODULE***********-->
<!--			<plugin>-->
<!--				<groupId>org.springframework.boot</groupId>-->
<!--				<artifactId>spring-boot-maven-plugin</artifactId>-->
<!--				<configuration>-->
<!--					<excludes>-->
<!--						<exclude>-->
<!--							<groupId>org.projectlombok</groupId>-->
<!--							<artifactId>lombok</artifactId>-->
<!--						</exclude>-->
<!--					</excludes>-->
<!--				</configuration>-->
<!--			</plugin>-->
		</plugins>
```
what this does is, it makes the project a regular spring boot project, and you will have import error 
that the classes are not found in the common module. although you have the shared classes available in the common module.

i only found this when i was inspecing the jar file that why the classes are not found, i do remember the classes are there.
then i saw the classes are inside this
$ jar tf common_module-3.4.3.jar | grep ResponderReservedAndNotifiedEvent
BOOT-INF/classes/com/emergency/roadside/help/common_module/saga/events/ResponderReservedAndNotifiedEvent$ResponderReservedAndNotifiedEventBuilder.class
BOOT-INF/classes/com/emergency/roadside/help/common_module/saga/events/ResponderReservedAndNotifiedEvent.class

if i use the spring boot maven plugin. 
when i get rid of this in the common module, rebuild the common module
$ jar tf common_module-3.4.3.jar | grep ResponderReservedAndNotifiedEvent
com/emergency/roadside/help/common_module/saga/events/ResponderReservedAndNotifiedEvent$ResponderReservedAndNotifiedEventBuilder.class
com/emergency/roadside/help/common_module/saga/events/ResponderReservedAndNotifiedEvent.class
