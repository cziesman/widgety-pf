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
Create the ArgoCD instance in the `argocd` namespace:
```shell
oc apply -f devops/argocd/00-argocd.yaml
```
Create the resources in the `widgety-pipelines` namespace for the Tekton pipeline that builds the application in the `dev` namespace:
```shell
oc apply -f devops/dev/tekton
```
Create the resources in the `widgety-pipelines` namespace for the Tekton pipeline that builds the application in the `qa` namespace:
```shell
oc apply -f devops/qa/tekton
```
Create the ArgoCD application that manages `dev` build pipelines in the `widgety-pipelines` namespace:
```shell
oc apply -f devops/argocd/01-pipeline.yaml
```
Create the ArgoCD application that manages `qa` build pipelines in the `widgety-pipelines` namespace:
```shell
oc apply -f devops/argocd/02-pipeline.yaml
```
Create the resources in the `widgety-pf-dev` namespace for the `widgety-pf` application:
```shell
oc apply -f devops/dev/application
```
Create the resources in the `widgety-pf-qa` namespace for the `widgety-pf` application:
```shell
oc apply -f devops/qa/application
```
Get the Quay secret for the robot account:
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: cziesman-cetars-demo-pull-secret
data:
  .dockerconfigjson: 
    <--- clipped --->
type: kubernetes.io/dockerconfigjson
```
Use the web console to create a `Secret` using the YAML from Quay. Make sure to include the `widgety-pipelines` namespace.
```yaml
  apiVersion: v1
  kind: Secret
  metadata:
    name: cziesman-cetars-demo-pull-secret
    namespace: widgety-pf-dev
  data:
    .dockerconfigjson:
      <--- clipped --->
  type: kubernetes.io/dockerconfigjson
```
Link the new secret to the `pipeline` service account:
```shell
oc secrets link pipeline cziesman-cetars-demo-pull-secret --for=mount -n widgety-pipelines
```
Grant an extra permission to the `pipeline` service account so that Tekton can manage the new pipelines:
```shell
oc policy add-role-to-user edit system:serviceaccount:widgety-pipelines:pipeline
```

# Operation

The build pipelines send their completion status to a Slack channel.

Setting up the Slack channel is outside the scope of this demo. However, once the channel is created, its OAuth token needs to be stored in a secret so that the pipeline can send the completion status.

Create a Secret as follows:
```yaml
kind: Secret
apiVersion: v1
metadata:
  name: slack-token
  namespace: widgety-pipelines
stringData:
  token: <--- clipped --->
```