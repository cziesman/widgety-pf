# Overview

This repository provides a devops demo that uses Tekton and ArgoCD to build and deploy a simple Spring Boot application to an Openshift cluster.

The setup includes separate namespaces for `dev`, `qa`, and `prod` environments in order to simulate a typical development, test, and deployment scenario.

# Installation
Install the Red Hat Openshift GitOps operator via the web console.

Install the Red Hat Openshift Pipelines operator v1.16.0 via the web console.

Create the `argocd`, `tekton-pipelines`, `widgety-pf-dev`, `widgety-pf-qa`, and `widgety-pf` namespaces:
```shell
oc apply -f devops/namespace
```
Create the ArgoCD instance in the `argocd` namespace:
```shell
oc apply -f devops/argocd/00-argocd.yaml
```
Create the common resources in the `tekton-pipelines` namespace for the Tekton pipelines that build the application in all namespace:
```shell
oc apply -f devops/common/tekton
```
Create the resources in the `tekton-pipelines` namespace for the Tekton pipeline that builds the application in the `dev` namespace:
```shell
oc apply -f devops/dev/tekton
```
Create the resources in the `tekton-pipelines` namespace for the Tekton pipeline that builds the application in the `qa` namespace:
```shell
oc apply -f devops/qa/tekton
```
Create the resources in the `tekton-pipelines` namespace for the Tekton pipeline that builds the application in the `prod` namespace:
```shell
oc apply -f devops/prod/tekton
```
Create the ArgoCD application that manages `dev` build pipelines in the `tekton-pipelines` namespace:
```shell
oc apply -f devops/argocd/01-pipeline.yaml
```
Create the ArgoCD application that manages `qa` build pipelines in the `tekton-pipelines` namespace:
```shell
oc apply -f devops/argocd/02-pipeline.yaml
```
Create the ArgoCD application that manages `prod` build pipelines in the `tekton-pipelines` namespace:
```shell
oc apply -f devops/argocd/03-pipeline.yaml
```
Create the resources in the `widgety-pf-dev` namespace for the `widgety-pf` application:
```shell
oc apply -f devops/dev/application
```
Create the resources in the `widgety-pf-qa` namespace for the `widgety-pf` application:
```shell
oc apply -f devops/qa/application
```
Create the resources in the `widgety-pf` namespace for the `widgety-pf` application:
```shell
oc apply -f devops/prod/application
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
Use the web console to create a `Secret` using the YAML from Quay. Make sure to include the `tekton-pipelines` namespace.
```yaml
  apiVersion: v1
  kind: Secret
  metadata:
    name: cziesman-cetars-demo-pull-secret
  namespace: tekton-pipelines
  data:
    .dockerconfigjson:
      <--- clipped --->
  type: kubernetes.io/dockerconfigjson
```
Link the new secret to the `pipeline` service account:
```shell
oc secrets link pipeline cziesman-cetars-demo-pull-secret --for=mount -n tekton-pipelines
```
Grant an extra permission to the `pipeline` service account so that Tekton can manage the new pipelines:
```shell
oc policy add-role-to-user edit system:serviceaccount:tekton-pipelines:pipeline
```
Create a secret containing the webhook token needed to authenticate the webhook call from Github:
```yaml
kind: Secret
apiVersion: v1
metadata:
  name: widgety-pf-github-webhook-secret
  namespace: tekton-pipelines
stringData:
  token: foobar
type: Opaque
```
The token value is arbitrary, and just needs to match the webhook secret that is configured in Github.
# Operation

The build pipelines send their completion status to a Slack channel.

Setting up the Slack channel is outside the scope of this demo. However, once the channel is created, its OAuth token needs to be stored in a secret so that the pipeline can send the completion status.

Create a Secret as follows:
```yaml
kind: Secret
apiVersion: v1
metadata:
  name: slack-token
  namespace: tekton-pipelines
stringData:
  token: <--- clipped --->
```

