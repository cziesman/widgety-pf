# build instructions



    oc label namespace demo-dev argocd.argoproj.io/managed-by=argocd

    oc get secret -n argocd argocd-cluster -o jsonpath='{.data.admin\.password}' | base64 --decode