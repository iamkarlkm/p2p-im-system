# IM Backend Kubernetes Deployment

Complete Kubernetes deployment configuration for the Instant Messaging System Backend.

## Overview

This directory contains Kubernetes manifests for deploying the IM Backend in a production Kubernetes cluster. The configuration includes:

- **Deployment** with rolling updates and pod anti-affinity
- **Horizontal Pod Autoscaler (HPA)** for automatic scaling
- **Pod Disruption Budget (PDB)** for high availability
- **Canary Release** configuration for safe deployments
- **RBAC** permissions and service accounts
- **Network Policies** for security
- **Namespace** configuration with resource quotas

## Prerequisites

- Kubernetes cluster v1.24+
- Helm v3.8+
- Istio v1.18+ (for canary releases)
- Flagger v1.30+ (for automated canary analysis)
- Prometheus Operator (for metrics)
- Cert-Manager (for TLS certificates)

## File Structure

```
kubernetes/
├── deployment.yaml           # Main deployment, services, ingress, configmaps
├── hpa.yaml                 # Horizontal Pod Autoscaler configurations
├── pdb.yaml                 # Pod Disruption Budget configurations
├── canary.yaml              # Canary release and Istio configurations
├── rbac.yaml                # RBAC roles, bindings, and service accounts
├── namespace.yaml           # Namespace, network policies, and quotas
├── README.md               # This documentation
├── apply-all.sh            # Deployment script
└── kustomization.yaml      # Kustomize base configuration
```

## Deployment

### 1. Create Namespace

```bash
kubectl apply -f namespace.yaml
```

### 2. Deploy RBAC

```bash
kubectl apply -f rbac.yaml
```

### 3. Deploy Main Configuration

```bash
kubectl apply -f deployment.yaml
```

### 4. Configure Auto-scaling

```bash
kubectl apply -f hpa.yaml
```

### 5. Configure High Availability

```bash
kubectl apply -f pdb.yaml
```

### 6. (Optional) Setup Canary Releases

```bash
kubectl apply -f canary.yaml
```

## Quick Deployment Script

Use the included script for complete deployment:

```bash
chmod +x apply-all.sh
./apply-all.sh
```

## Configuration Details

### Deployment Strategy

The deployment uses a rolling update strategy with:
- `maxSurge: 1` (one pod can be created above desired count during update)
- `maxUnavailable: 0` (no pods become unavailable during update)
- Graceful shutdown with 30-second pre-stop hook

### Resource Requirements

| Component | CPU Request | CPU Limit | Memory Request | Memory Limit |
|-----------|-------------|-----------|----------------|--------------|
| Main Pod | 500m | 2 | 1Gi | 4Gi |
| Canary Pod | 500m | 1 | 1Gi | 2Gi |

### Horizontal Pod Autoscaler

HPA scales based on:
1. **CPU Utilization**: Target 70%
2. **Memory Utilization**: Target 80%
3. **Custom Metrics**: 
   - Messages per second: Target 1000
   - Active connections: Target 5000
   - HTTP requests per second: Target 100
   - WebSocket connections: Target 500

Scaling behavior:
- **Scale Up**: Aggressive (2 pods or 100% increase per minute)
- **Scale Down**: Conservative (1 pod or 10% decrease per 5 minutes)

### Pod Disruption Budget

- **Min Available**: 2 pods
- **Max Unavailable**: 1 pod (for critical operations)
- Graceful shutdown timeout: 300 seconds

### Canary Release Strategy

Progressive canary release with 5 stages:
1. **Initialization** (5 min, 0% traffic): Health validation
2. **Validation** (15 min, 10% traffic): Load testing
3. **Ramp-up** (30 min, 25% traffic): Performance testing
4. **Production** (60 min, 50% traffic): Canary analysis
5. **Full Release** (120 min, 100% traffic): Final validation

### Network Security

Default network policy: **Deny All**
Allowed ingress:
- From Istio namespace (ports 8080, 8081, 8082)
- From monitoring namespace (ports 8081, 8082)
- Internal pod communication (ports 8080, 8081)

Allowed egress:
- To database namespace (MySQL 3306)
- To cache namespace (Redis 6379)
- To monitoring namespace (Prometheus 9090)
- External APIs (HTTPS 443, HTTP 80)

### Monitoring and Metrics

The deployment exposes metrics on port 8082:
- Spring Boot Actuator metrics
- Custom IM metrics
- Prometheus format

Key metrics to monitor:
- `im_messages_sent_total`: Total messages sent
- `im_active_connections`: Active WebSocket connections
- `http_request_duration_seconds`: HTTP request latency
- `container_cpu_usage_seconds_total`: CPU usage
- `container_memory_working_set_bytes`: Memory usage

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Spring profiles | `kubernetes,production` |
| `JAVA_OPTS` | JVM options | G1GC with 2GB heap |
| `MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE` | Actuator endpoints | `health,info,metrics,prometheus` |
| `MYSQL_USERNAME` | Database username | From secret |
| `MYSQL_PASSWORD` | Database password | From secret |

## Secrets Management

Secrets should be created in the `im-system` namespace:

```bash
# Create MySQL secret
kubectl create secret generic im-mysql-secret \
  --namespace im-system \
  --from-literal=username=im_user \
  --from-literal=password=$(openssl rand -base64 32)

# Create application secret
kubectl create secret generic im-backend-secret \
  --namespace im-system \
  --from-literal=encryption-key=$(openssl rand -base64 32) \
  --from-literal=jwt-secret=$(openssl rand -base64 64)
```

## Storage

The deployment uses:
- **ConfigMap**: Application configuration
- **EmptyDir**: Logs and temporary storage
- **PersistentVolumeClaim** (optional): For persistent data

## High Availability Features

1. **Pod Anti-Affinity**: Pods scheduled on different nodes
2. **Node Affinity**: Preference for compute-optimized nodes
3. **Readiness Probes**: 5-second interval with 3 failures
4. **Liveness Probes**: 10-second interval with 3 failures
5. **Startup Probes**: 30 failures allowed for slow startup

## Disaster Recovery

### Backup Procedure

```bash
# Backup Kubernetes resources
velero backup create im-backend-backup --include-namespaces im-system

# Backup databases
mysqldump -h mysql.im-system.svc.cluster.local -u im_user im_db > backup.sql

# Backup persistent volumes
kubectl exec -n im-system deployment/im-backend -- tar czf /tmp/backup.tar.gz /app/data
```

### Recovery Procedure

```bash
# Restore Kubernetes resources
velero restore create --from-backup im-backend-backup

# Restore database
mysql -h mysql.im-system.svc.cluster.local -u im_user im_db < backup.sql

# Scale up deployment
kubectl scale -n im-system deployment/im-backend --replicas=3
```

## Troubleshooting

### Common Issues

1. **Pods stuck in Pending state**
   ```bash
   kubectl describe pod -n im-system im-backend-xxxxx
   kubectl get events -n im-system --sort-by='.lastTimestamp'
   ```

2. **Pods crash looping**
   ```bash
   kubectl logs -n im-system deployment/im-backend --previous
   kubectl describe pod -n im-system im-backend-xxxxx
   ```

3. **High CPU/Memory usage**
   ```bash
   kubectl top pods -n im-system
   kubectl get hpa -n im-system
   ```

4. **Network connectivity issues**
   ```bash
   kubectl run -n im-system debug --rm -i --tty --image=nicolaka/netshoot -- /bin/bash
   # Inside container:
   curl http://im-backend-service:8080/health
   nc -zv mysql.im-system.svc.cluster.local 3306
   ```

### Health Checks

```bash
# Check pod status
kubectl get pods -n im-system -l app=im-backend

# Check service endpoints
kubectl get endpoints -n im-system im-backend-service

# Check ingress
kubectl get ingress -n im-system

# Check HPA status
kubectl get hpa -n im-system

# Check PDB status
kubectl get pdb -n im-system

# Check resource usage
kubectl top pods -n im-system
```

### Logs

```bash
# View logs for all pods
kubectl logs -n im-system -l app=im-backend --tail=100

# View logs for specific pod
kubectl logs -n im-system deployment/im-backend

# Follow logs
kubectl logs -n im-system -l app=im-backend -f

# View previous container logs
kubectl logs -n im-system deployment/im-backend --previous
```

## Performance Tuning

### JVM Options

Default JVM options:
```bash
-XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:InitiatingHeapOccupancyPercent=45 -Xmx2G -Xms2G
```

### Database Connection Pool

HikariCP configuration:
- Maximum pool size: 20
- Minimum idle: 5
- Connection timeout: 30s
- Idle timeout: 600s
- Max lifetime: 1800s

### WebSocket Configuration

- Maximum connections per pod: 10000
- Idle timeout: 3600s
- Message size limit: 50MB

## Security Hardening

### Security Context

```yaml
securityContext:
  runAsUser: 1000
  runAsGroup: 1000
  fsGroup: 1000
  runAsNonRoot: true
  readOnlyRootFilesystem: true
  capabilities:
    drop:
      - ALL
```

### Image Security

- Use signed images from trusted registry
- Scan images for vulnerabilities
- Use minimal base images (e.g., distroless Java)

### Network Security

- Enable Istio mTLS
- Use network policies
- Restrict egress traffic
- Enable audit logging

## Cost Optimization

### Right-sizing

1. **CPU**: Start with 500m, monitor usage
2. **Memory**: Start with 1Gi, monitor usage
3. **Replicas**: Minimum 2, maximum based on HPA

### Auto-scaling

- Enable scheduled scaling for business hours
- Use predictive scaling if available
- Set appropriate min/max replicas

### Resource Utilization Targets

- CPU: 70% target utilization
- Memory: 80% target utilization
- Storage: 80% target utilization

## Maintenance

### Rolling Updates

```bash
# Trigger rolling update
kubectl rollout restart deployment/im-backend -n im-system

# Monitor rollout status
kubectl rollout status deployment/im-backend -n im-system

# Rollback if needed
kubectl rollout undo deployment/im-backend -n im-system
```

### Scale Operations

```bash
# Scale up
kubectl scale deployment/im-backend -n im-system --replicas=5

# Scale down
kubectl scale deployment/im-backend -n im-system --replicas=2
```

### Node Maintenance

```bash
# Drain node
kubectl drain <node-name> --ignore-daemonsets --delete-emptydir-data

# Uncordon node
kubectl uncordon <node-name>
```

## Monitoring and Alerts

### Key Metrics Dashboard

Create a Grafana dashboard with:
1. **CPU/Memory Usage** per pod
2. **Request Rate** and **Error Rate**
3. **Latency** percentiles (p50, p95, p99)
4. **Active Connections** and **Message Throughput**
5. **HPA Status** and **Scaling Events**

### Alert Rules

Configure Prometheus alerts for:
- **High Error Rate**: >1% for 5 minutes
- **High Latency**: p99 > 2s for 5 minutes
- **Low Availability**: <2 pods available
- **Resource Exhaustion**: CPU > 90% or Memory > 95%
- **HPA Max Replicas**: Reached max replicas

## Support

For issues with this deployment:
1. Check the troubleshooting section
2. Review Kubernetes events
3. Examine application logs
4. Contact platform-team@example.com

## Changelog

### v1.0.0 (2026-03-22)
- Initial Kubernetes deployment configuration
- HPA with CPU, memory, and custom metrics
- PDB for high availability
- Canary release configuration
- RBAC and network policies
- Comprehensive documentation