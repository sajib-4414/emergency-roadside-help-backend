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
3. Not related right now, but: you can have some code in springboot with @profile('dev')/test like that. it means it has the highest priority,
and this code will never be executed if the environment mentioned here, is not explicitly activated right now. this config has higher priority.
Where is this useful: some code that should be only run in production, or only in test. very sensitive areas or areas where a particular environment
does not have data/configuration so it will fail. for example, some third partyy integration sometime only meant to be used with real data,
then we can specify @profile(prod) so, we never ever run that method that invokes third party with dummy/test data.

---------
Repository Unit testing:
we use @datajpatest it creates h2 database for testing. it does not load the whole spring context but just as needed
for repository testing.
if u use both @Activeprofiletest and @datajpatest. activeprofiles takes precedence and replaces anything applicable.
following this article
https://medium.com/javarevisited/getting-started-with-unit-testing-in-spring-boot-bada732a5baa

do u need transactional and rollback?
Scenario	Required Annotations
Default: all DB changes rolled back after test	@Transactional only is sufficient, u can still put rollback/rollback(true), wont harm
Want to commit changes from a test (rare cases)	@Transactional plus @Rollback(false) or @Commit

so in my case testcase was crashing because i had a year column in vehcile table, although changed later. but in h2 database
year is a reserved keywrod, so it wont allow running the migration to the point where i changed the year column name.
i tried putting the
You can add ;NON_KEYWORDS=VALUE to JDBC URL.
did not work. so in test i disabled flyaway and let jdbc create everything with ddl-drop

## How to have code coverage?
I am using Jacoco plugin for test coverage, i was having some issues, then i had to upgrade to latest version then error was gone.
with jacoco in the maven, now just run mvnw clean verify, and the test report would be in the target->jacoco->index.hml
