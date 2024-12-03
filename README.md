# Overview

This repository provides a devops demo that uses Tekton and ArgoCD to build and deploy a simple Spring Boot application to an Openshift cluster.

The setup includes separate namespaces for `dev`, `qa`, and `prod` environments in order to simulate a typical development, test, and deployment scenario.

# Installation
Install the Red Hat Openshift GitOps operator via the web console.

Install the Red Hat Openshift Pipelines operator via the web console.

Create the `argocd`, `widgety-pipelines`, `widgety-pf-dev`, and `widgety-pf-qa` namespaces:
```
oc apply -f devops/namespace
```
Create the resources in the `argocd` namespace for the ArgoCD application that builds the application in the `dev` namespace:
```
oc apply -f devops/dev/tekton
```
Create the resources in the `argocd` namespace for the ArgoCD application that builds the application in the `qa` namespace:
```
oc apply -f devops/qa/tekton
```
Create the ArgoCD application that manages `dev` build pipelines in the `widgety-pipelines` namespace:
```
oc apply -f devops/dev/argocd/02-pipeline.yaml
```
Create the ArgoCD application that manages `qa` build pipelines in the `widgety-pipelines` namespace:
```
oc apply -f devops/qa/argocd/02-pipeline.yaml
```
Create the resources in the `widgety-pf-dev` namespace for the `widgety-pf` application:
```
oc apply -f devops/dev/application
```
Create the resources in the `widgety-pf-qa` namespace for the `widgety-pf` application:
```
oc apply -f devops/qa/application
```

# Operation

Get the pipeline docker config secret:
```
oc get secrets -n widgety-pipelines | grep pipeline

pipeline-dockercfg-vfq87           kubernetes.io/dockercfg          1      6h6m
```
Using the name of the pipeline docker config, get the token used to authenticate against the Openshift internal registry:
```
oc get secret pipeline-dockercfg-vfq87 -o jsonpath='{.data.\.dockercfg}' | base64 --decode | jq -r '."image-registry.openshift-image-registry.svc:5000".auth' | base64 --decode

<token>:eyJhbGciOiJSUzI1NiIsImtpZCI6IktRbEtHeWd1dnZFd3pSNG9qQi1UenpFSVVZZFlTSjR0U29sUFIzMFVwOWcifQ.eyJhdWQiOlsiaHR0cHM6Ly9rdWJlcm5ldGVzLmRlZmF1bHQuc3ZjIl0sImV4cCI6MTczMzI2NTE0NiwiaWF0IjoxNzMzMjYxNTQ2LCJpc3MiOiJodHRwczovL2t1YmVybmV0ZXMuZGVmYXVsdC5zdmMiLCJqdGkiOiIyNTNhNzRmZi00NTcxLTQ1ZTktYjU0NC00MTAzNDBlNWRkNjUiLCJrdWJlcm5ldGVzLmlvIjp7Im5hbWVzcGFjZSI6IndpZGdldHktcGlwZWxpbmVzIiwic2VjcmV0Ijp7Im5hbWUiOiJwaXBlbGluZS1kb2NrZXJjZmctdmZxODciLCJ1aWQiOiIwZDZlMTEzNS0xYjdlLTQ0MDQtYmY4ZS1lODY4YTVkZGFjMjcifSwic2VydmljZWFjY291bnQiOnsibmFtZSI6InBpcGVsaW5lIiwidWlkIjoiNWU1YTUwMDUtY2Y3ZS00YTBmLTk0MWEtM2JjN2U2NmZhODM0In19LCJuYmYiOjE3MzMyNjE1NDYsInN1YiI6InN5c3RlbTpzZXJ2aWNlYWNjb3VudDp3aWRnZXR5LXBpcGVsaW5lczpwaXBlbGluZSJ9.OYYANXEafBEEqY301ynnRjfauc8CkTi-Ag4j-pNb_Z7YJg9Mon5wOI6OTUYJ_sTcht4HhOrWvIoZ8vFC3PRmtxFD1oCrzheoMI7GS12-aJr2MTeBEUHFUoaX-_K4ooPS2OAt8bIR_l2lb7lKm5akHKA4Itaj8qEoV-Hye5UcoGUIXzWeJ9rtXudqOw-8FcGLSlfO2Nx59lnL2t8HgKQ3ygVpRxMfoMF-cie6Yq-Fmr2hjoL2FehgL8Vqw5TsNbv44Kf8bHxchSEfq_k8xl7xkABOgQi4SYaWirh7UF_DVgsZaXPNCTZX1-u5MVnCiNET2--so6jFcmz_aYk9s9ElmXKCoXWb44fSuw92huaSlGOiy8mP5AWSHCJWRReBG98PYUQL6HkrpEvxqZtfIC0rQtcZOajvOnIYaXblqw2H2KwkeSOEMm7CllL4ED4VPJweOuMA86_gePf1wDBc8e-iXev9yf8oqhyf5OBe4s0alaceOs6b01NjaU5uDQEDTjJe6A90MC2ndYjWB-d7Fu8zzQukJLPDzuW38IhzsPH45nbhCrjdojwtGyOBtLt__oXvgqyQ2JvmzOvwEJj4TyBj7Qp7rHPzI4NwFB6Nt9gPdk9xBf_zy-mw6DoS_J0KnE7ZIZ0q0UwrVhOi_Jkf7t69K_-hePUpTUl8ChK2wrpldBM
```

Use the web console to create an `Image Pull Secret` in the `widgety-pipelines` project using the following values:

Secret name: `pipeline-internal-registry`

Registry server address: `image-registry.openshift-image-registry.svc:5000`

Username : `pipeline`

Password : The contents of the `<token>` field from the previous command

In order for Tekton to manage the new pipelines, we need to grant an extra permission to the `pipeline` service account:
```
oc policy add-role-to-user edit system:serviceaccount:widgety-pipelines:pipeline
```


