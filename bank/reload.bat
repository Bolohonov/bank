helm uninstall bank
kubectl delete pvc data-bank-postgresql-0
kubectl delete pvc data-bank-kafka-broker-0
kubectl delete pvc data-bank-kafka-controller-0
helm install bank ./
