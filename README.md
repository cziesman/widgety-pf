# Overview

This repository provides a devops demo that uses Tekton and ArgoCD to build and deploy a simple Spring Boot application to an Openshift cluster.

The setup includes separate namespaces for `dev`, `qa`, and `prod` environments in order to simulate a typical development, test, and deployment scenario.

# Installation
Create the `widgety-pipelines` namespace: 
```
    oc apply -f devops/00-namespace.yaml
```
Create the `widgety-pf-dev` namespace:
```
    oc apply -f devops/dev/00-namespace.yaml
```
Create the `widgety-pf-qa` namespace:
```
    oc apply -f devops/qa/00-namespace.yaml
```
Create the ArgoCD application that manages `dev` deployments in the `argocd` namespace:
```
    oc apply -f devops/dev/argocd/01-application.yaml
```
Create the ArgoCD application that manages `qa` deployments in the `argocd` namespace:
```
    oc apply -f devops/qa/argocd/01-application.yaml
```
Create the ArgoCD application that manages `dev` build pipelines in the `widgety-pipelines` namespace:
```
    oc apply -f devops/dev/argocd/01-pipelines.yaml
```
Create the ArgoCD application that manages `qa` build pipelines in the `widgety-pipelines` namespace:
```
    oc apply -f devops/qa/argocd/01-pipelines.yaml
```

# Operation
In order for Tekton to manage the new pipelines, we need to grant an extra permission to the `pipeline` service account:
```
    oc policy add-role-to-user edit system:serviceaccount:widgety-pipelines:pipeline
```
