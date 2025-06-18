# Custom Authenticator and Adaptive Script Functions for WSO2 Identity Server 
_(this project's code is based on v5.11.0)_

---

This project provides a custom authenticator that saves properties to the session context during authentication, 
and a corresponding JavaScript function to retrieve these properties within an adaptive script. This enhances adaptive 
authentication flows by allowing administrators to make granular decisions based on runtime values that are typically 
only available within the authenticator's scope.

Please see each module's README file for additional implementation insights. 

---

### Build

To build the project, navigate to the root directory and run:

```bash
  mvn clean install
```

### Deployment

* Once built, move `authenticator/target/org.sample.authenticator-1.0.0-SNAPSHOT.jar` and 
`functions/target/org.sample.adaptive.script.functions-1.0.0-SNAPSHOT.jar` to the `<IS_HOME>/repository/components/dropins/` directory.
* Then, add the configuration below to the `<IS_HOME>/repository/conf/deployment.toml` file to enable the custom authenticator.
```toml
[[authentication.custom_authenticator]]
name = "CustomAuthenticator"
```
* Finally, restart the Identity Server.

--- 

### Usage

* Once the custom authenticator is deployed, go to the Management Console (e.g. https://localhost:9443/carbon).
* Then, navigate to your service provider and open "Advanced Configuration" under the "Local and Outbound Authentication" section.
* Make sure the custom authenticator is selected as the authenticator for one of the authentication steps.
* You can now use the custom function `getCustomPropertiesFromContext` in the "Script Based Adaptive Authentication" section to retrieve 
the properties saved by the custom authenticator (i.e. `PROCESS_KEY`, `EXECUTE_ATLF_KEY`, `INITIATE_AUTHREQ_KEY`, `PROCESS_AUTHRES_KEY`).
  * Example script:
```javascript
var PROCESS_KEY_CONST = "PROCESS_KEY";

var onLoginRequest = function(context) {
    executeStep(1, {
        onSuccess: function(context) {
            var customProperties = getCustomPropertiesFromContext(context);
            
            var processKey = customProperties[PROCESS_KEY_CONST];
            
            if (proccessKey && processKey === "VALUE") {
                executeStep(2);
            }
        }
    });
};
```
