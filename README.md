# Overview

This repository provides a devops demo that uses Tekton and ArgoCD to build and deploy a simple Spring Boot application to an Openshift cluster.

The setup includes separate namespaces for `dev`, `qa`, and `prod` environments in order to simulate a typical development, test, and deployment scenario.

# Installation
Install the Red Hat Openshift GitOps operator via the web console.

Install the Red Hat Openshift Pipelines operator via the web console.

Create the `argocd`, `widgety-pipelines`, `widgety-pf-dev`, and `widgety-pf-qa` namespaces:
```shell
oc apply -f devops/namespace
```
Create the resources in the `argocd` namespace for the ArgoCD application that builds the application in the `dev` namespace:
```shell
oc apply -f devops/dev/tekton
```
Create the resources in the `argocd` namespace for the ArgoCD application that builds the application in the `qa` namespace:
```shell
oc apply -f devops/qa/tekton
```
Create the ArgoCD application that manages `dev` build pipelines in the `widgety-pipelines` namespace:
```shell
oc apply -f devops/dev/argocd/02-pipeline.yaml
```
Create the ArgoCD application that manages `qa` build pipelines in the `widgety-pipelines` namespace:
```shell
oc apply -f devops/qa/argocd/02-pipeline.yaml
```
Create the resources in the `widgety-pf-dev` namespace for the `widgety-pf` application:
```shell
oc apply -f devops/dev/application
```
Create the resources in the `widgety-pf-qa` namespace for the `widgety-pf` application:
```shell
oc apply -f devops/qa/application
```

# Operation

Get the Quay secret for the robot account:
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: cziesman-cetars-demo-pull-secret
data:
  .dockerconfigjson: ewogICJhdXRocyI6IHsKICAgICJxdWF5LmlvIjogewogICAgICAiYXV0aCI6ICJZM3BwWlhOdFlXNHJZMlYwWVhKelgyUmxiVzg2VDBNd1IxRkdPRWRHTkRCSFUwMDRNekJLVVVjMlVURkZPRTFGTnpWV1NWaElSa1JKVGxsS1NrUktTRTFKVEVkRFdEWlFURXRUU3pCTVUwSTVOelZDU3c9PSIsCiAgICAgICJlbWFpbCI6ICIiCiAgICB9CiAgfQp9
type: kubernetes.io/dockerconfigjson
```
Use the web console to create a `Secret` using the YAML from Quay. Make sure to add the `widgety-pipelines` namespace.
```yaml
  namespace: widgety-pf-dev
```
Link the new secret to the `pipeline` service account:
```shell
oc secrets link pipeline cziesman-cetars-demo-pull-secret --for=mount -n widgety-pipelines
```

In order for Tekton to manage the new pipelines, we need to grant an extra permission to the `pipeline` service account:
```shell
oc policy add-role-to-user edit system:serviceaccount:widgety-pipelines:pipeline
```


