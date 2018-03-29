# kiali-qe-java
Selenium/Java UI tests for Kiali

### run tests
```
mvn test -DkialiHostname="localhost" -DseleniumGrid="http://localhost:4444/wd/hub"
```

### define blockers list
We can define blockers list out of the box, without touch actual tests. Create a yaml file as specified here,

```yaml
--- # blockers list
# bug tracker tools details
bug-trackers:
  jira:
    url: https://issues.jboss.org
    non-blocking-list:
      - ready

# blockers
blockers:
  com.redhat.qe.kiali.ui.tests.menu.TestMenu.testToggle:
    - JR:KIALI-429
  com.redhat.qe.kiali.ui.tests.menu.TestNavbar.testAbout:
    - JR:KIALI-430
```
###### definitions:
* `bug-trackers`: define jira tracking tool details. For now supports only for jira
* `non-blocking-list`: list of status key words, say this issue is resolved and ready to test. This keys words should be in small case. 
* `blockers`: we can specify `testClass` name or `testMethod`(fully qualified name) as a key and a list of bugs/issues details to block this test. If we specify `className`, it blocks all the methods on this class.

###### How to supply this yaml file?
We can supply this yaml file in different ways,
* Source code: update `src/test/resources/blockers.yaml` file.
* Java properties: `-Dblockers=/tmp/blockers.yaml`
* Environment variable: `export blocers="/tmp/blockers.yaml"`