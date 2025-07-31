need to have test applicaiton yml so that testing go through only h2 database. have that h2 database and all other config in test application yml

there are several way of using  profile/profile for testing:
1. you can have application yml, application test yml, application prod yml, then in the applicaiton yml you can specify which profile are you using.
what happens is, the application yml works as a base yml, and the config that you are activating, is the extra config that is added. 
if there are same keys, spring will replace/override with the specific profile you are saying.
in this approach, before testing, you replace the profile name in applicaiton yml to ensure all test setups are picked up.
but this way, you need to keep changing in the applicaiton yml everytime. dev to test to prod, even for testing.
of course you will have dev, prod, but for testing its cumbersome.
2.  you can to say in the testclass that activateProfile(test) to activate the test profile. then it will pickup the application yml test,
on the runtime. but u need to do this for every test class, make sure to do.
3. Not related right now, but: you can have some code in springboot Normal applicaiton code with @profile('dev')/test like that. it means it has the highest priority,
and this code will never be executed if the environment mentioned here, is not explicitly activated right now. this config has higher priority.
Where is this useful: some code that should be only run in production, or only in test. very sensitive areas or areas where a particular environment
does not have data/configuration so it will fail. for example, some third partyy integration sometime only meant to be used with real data,
then we can specify @profile(prod) so, we never ever run that method that invokes third party with dummy/test data.

---------
## Unit testing
Repository Unit testing:
we use @datajpatest it creates h2 database for testing. it does not load the whole spring context but just as needed
for repository testing. Datajpatest is supposed to autoconfigure an h2 database and just replace the other databases. 
if u use both @Activeprofiletest and @datajpatest. activeprofiles(test) takes precedence and replaces anything applicable
from the aapplication test yml.
following this article
https://medium.com/javarevisited/getting-started-with-unit-testing-in-spring-boot-bada732a5baa

do u need transactional and rollback?
Scenario	Required Annotations
Default: all DB changes rolled back after test	@Transactional only is sufficient, u can still put rollback/rollback(true), wont harm
Want to commit changes from a test (rare cases)	@Transactional plus @Rollback(false) or @Commit

service layer testing:
we use mock from mockito, this is purely service layer testing, mockito is used in collaboration with Junit

controller layer testing:
we use webmvc test thats from the springboot testing, and for that we use spring boot test's mockbean
here we are mostly using spring boot test's utilities

**Reserved keyword issue in h2 database for testing**
so in my case testcase was crashing because i had a year column in vehcile table, although changed later. but in h2 database
year is a reserved keywrod, so it wont allow running the migration to the point where i changed the year column name.
i tried putting the
You can add ;NON_KEYWORDS=VALUE to JDBC URL.
did not work. so in test i disabled flyaway and let jdbc create everything with ddl-drop

i used first only datajpatest, but it creates an h2 database keeps all the other settings intact, even i try to disable flyaway+hibernate ddl-auto:create drop in applicaiton yml(main)
still it does not obey. the result was it was crashing all the time because in the first migration i created a table with column 
name year. so the solution was to suppress this with activeprofile(test) which takes precendence and makes sure the applicaitontest yml
is applied and flayway disabled and ddl-auto:create from the applicaiton test.


## How to have code coverage?
I am using Jacoco plugin for test coverage, i was having some issues, then i had to upgrade to latest version then error was gone.
with jacoco in the maven, now just run mvnw clean verify, and the test report would be in the target->jacoco->index.hml

## How to mock?
* use java faker to mock the data, (generate mock/fake data). Not sure why its not working...
* use Mockito to generate mock Beans and other things. 
```java

@ExtendWith(MockitoExtension.class)
public class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleService vehicleService;

```

more testing resources
https://medium.com/simform-engineering/testing-spring-boot-applications-best-practices-and-frameworks-6294e1068516
https://medium.com/@Lakshitha_Fernando/spring-boot-unit-testing-for-repositories-controllers-and-services-using-junit-5-and-mockito-def3ff5891be

**Another interesting thing:**

as per chatgpt if u use activprofile to have a testenrivonment then its not unit test anymore its integration test.
have to verify. claude says its in between, claude says this
That's a great question that touches on the ongoing debate about test classification and boundaries in software testing.
Technically, once you're using a test profile with H2 and persisting/retrieving data from an actual database (even an in-memory one), you've moved beyond a pure unit test into integration test territory. Here's why:
Pure Unit Test Characteristics:

Tests a single unit (method/class) in isolation
All dependencies are mocked/stubbed
No external systems (databases, file systems, networks)
Fast execution
Deterministic

Your H2 Scenario:

Tests the interaction between your code and a database
Involves persistence layer, SQL execution, transaction management

**I have also noticed that spring tries to isolate as much as possible for Unit testing, but its debatable**
https://medium.com/javarevisited/getting-started-with-unit-testing-in-spring-boot-bada732a5baa

in this article when saying about unit test,
when he is testing repostiory he is using datajpatest which is supposed to test the repostiory code in isolation, just
for unit testing.
when he is tersting the services, he is using the
@ExtendWith(MockitoExtension.class) then mock to mock the repostiory and other beans and then injectmock to create an
instance of an service to test in isolation. means the service willbe created with the mocked beans, so only service
and mock are in isolation.
when he is testing the controller, he is using webmvc, mockmvc to have the service mocked, and testing the controller in 
isolation only with the mocked service.
it looks like spring has many tools to isolate and only test the thing one by one, when doing Unit testing.
so it feels like purely authentic unit testing.

claude said:
However, there's significant debate in the testing community about whether these are truly "unit tests":
Some people call these tests unit tests because they test a part of the application in isolation. However, 
the tests rely on the Spring application context which is why some people call them integration tests