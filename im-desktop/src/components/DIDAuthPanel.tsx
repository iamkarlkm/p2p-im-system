import React, { useState, useEffect, useCallback } from 'react';
import { QRCodeSVG } from 'qrcode.react';
import { 
  Shield, 
  Key, 
  CheckCircle, 
  XCircle, 
  Copy, 
  ExternalLink,
  Wallet,
  User,
  Building,
  Smartphone,
  Globe,
  MoreHorizontal,
  RefreshCw,
  LogOut,
  ChevronRight,
  AlertTriangle,
  FileText,
  Plus,
  Trash2,
  Download,
  Upload,
  ScanLine,
  Fingerprint,
  Lock,
  Unlock,
  Eye,
  EyeOff,
  Settings,
  HelpCircle,
  ArrowLeft,
  Loader2
} from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import DIDAuthService, { 
  DIDIdentity, 
  DIDMethod, 
  IdentityType, 
  VerificationStatus,
  VerifiableCredential,
  AuthSession,
  DIDDocument,
  IdentityMetadata
} from '../services/didAuthService';

// ==================== 类型定义 ====================

interface DIDAuthPanelProps {
  onAuthenticated?: (session: AuthSession) => void;
  onClose?: () => void;
  showCloseButton?: boolean;
  initialView?: AuthView;
}

type AuthView = 'main' | 'create' | 'import' | 'list' | 'detail' | 'scan' | 'settings';

type CreateStep = 'method' | 'type' | 'info' | 'keys' | 'backup';

interface IdentityCardProps {
  identity: DIDIdentity;
  isActive?: boolean;
  onSelect?: (identity: DIDIdentity) => void;
  onDelete?: (did: string) => void;
  onExport?: (did: string) => void;
}

interface CredentialCardProps {
  credential: VerifiableCredential;
  onVerify?: () => void;
  onRemove?: () => void;
}

interface QRScannerProps {
  onScan: (data: string) => void;
  onClose: () => void;
}

// ==================== 主组件 ====================

export const DIDAuthPanel: React.FC<DIDAuthPanelProps> = ({
  onAuthenticated,
  onClose,
  showCloseButton = true,
  initialView = 'main'
}) => {
  const [currentView, setCurrentView] = useState<AuthView>(initialView);
  const [identities, setIdentities] = useState<DIDIdentity[]>([]);
  const [selectedIdentity, setSelectedIdentity] = useState<DIDIdentity | null>(null);
  const [session, setSession] = useState<AuthSession | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const didService = DIDAuthService.getInstance();

  useEffect(() => {
    loadIdentities();
    const currentSession = didService.getActiveSession();
    if (currentSession) {
      setSession(currentSession);
    }

    const handleAuthSuccess = (data: { session: AuthSession }) => {
      setSession(data.session);
      onAuthenticated?.(data.session);
    };

    didService.on('auth:success', handleAuthSuccess);
    return () => {
      didService.off('auth:success', handleAuthSuccess);
    };
  }, []);

  const loadIdentities = () => {
    const allIdentities = didService.getAllIdentities();
    setIdentities(allIdentities);
  };

  const handleSelectIdentity = (identity: DIDIdentity) => {
    setSelectedIdentity(identity);
    setCurrentView('detail');
  };

  const handleAuthenticate = async (did: string) => {
    setIsLoading(true);
    setError(null);
    try {
      const newSession = await didService.authenticate(did, window.location.origin);
      setSession(newSession);
      setSuccess('Authentication successful!');
      setTimeout(() => setSuccess(null), 3000);
    } catch (err: any) {
      setError(err.message || 'Authentication failed');
    } finally {
      setIsLoading(false);
    }
  };

  const handleLogout = () => {
    didService.logout();
    setSession(null);
    setSuccess('Logged out successfully');
    setTimeout(() => setSuccess(null), 3000);
  };

  const handleDeleteIdentity = async (did: string) => {
    if (!confirm('Are you sure you want to delete this identity? This action cannot be undone.')) {
      return;
    }
    setIsLoading(true);
    try {
      await didService.deleteIdentity(did);
      loadIdentities();
      if (selectedIdentity?.did === did) {
        setSelectedIdentity(null);
        setCurrentView('list');
      }
      setSuccess('Identity deleted successfully');
    } catch (err: any) {
      setError(err.message || 'Failed to delete identity');
    } finally {
      setIsLoading(false);
    }
  };

  const renderContent = () => {
    switch (currentView) {
      case 'main':
        return (
          <MainView 
            session={session}
            identityCount={identities.length}
            onCreate={() => setCurrentView('create')}
            onImport={() => setCurrentView('import')}
            onList={() => setCurrentView('list')}
            onScan={() => setCurrentView('scan')}
            onSettings={() => setCurrentView('settings')}
            onLogout={handleLogout}
          />
        );
      case 'create':
        return (
          <CreateIdentityView
            onCreated={() => {
              loadIdentities();
              setCurrentView('list');
            }}
            onBack={() => setCurrentView('main')}
          />
        );
      case 'import':
        return (
          <ImportIdentityView
            onImported={() => {
              loadIdentities();
              setCurrentView('list');
            }}
            onBack={() => setCurrentView('main')}
          />
        );
      case 'list':
        return (
          <IdentityListView
            identities={identities}
            session={session}
            onSelect={handleSelectIdentity}
            onAuthenticate={handleAuthenticate}
            onDelete={handleDeleteIdentity}
            onBack={() => setCurrentView('main')}
            isLoading={isLoading}
          />
        );
      case 'detail':
        return selectedIdentity ? (
          <IdentityDetailView
            identity={selectedIdentity}
            session={session}
            onAuthenticate={() => handleAuthenticate(selectedIdentity.did)}
            onDelete={() => handleDeleteIdentity(selectedIdentity.did)}
            onBack={() => setCurrentView('list')}
            isLoading={isLoading}
          />
        ) : null;
      case 'scan':
        return (
          <ScanView
            onAuthenticated={(sess) => {
              setSession(sess);
              onAuthenticated?.(sess);
              setCurrentView('main');
            }}
            onBack={() => setCurrentView('main')}
          />
        );
      case 'settings':
        return (
          <SettingsView
            onBack={() => setCurrentView('main')}
          />
        );
      default:
        return null;
    }
  };

  return (
    <div className="did-auth-panel">
      <div className="panel-header">
        {currentView !== 'main' && (
          <button className="btn-back" onClick={() => setCurrentView('main')}>
            <ArrowLeft size={20} />
          </button>
        )}
        <div className="panel-title">
          <Shield className="icon-primary" size={24} />
          <span>DID Authentication</span>
        </div>
        {showCloseButton && onClose && (
          <button className="btn-close" onClick={onClose}>
            <XCircle size={24} />
          </button>
        )}
      </div>

      <AnimatePresence>
        {error && (
          <motion.div
            initial={{ opacity: 0, y: -20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20 }}
            className="alert alert-error"
          >
            <AlertTriangle size={18} />
            <span>{error}</span>
            <button onClick={() => setError(null)}><XCircle size={16} /></button>
          </motion.div>
        )}
        {success && (
          <motion.div
            initial={{ opacity: 0, y: -20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20 }}
            className="alert alert-success"
          >
            <CheckCircle size={18} />
            <span>{success}</span>
          </motion.div>
        )}
      </AnimatePresence>

      <div className="panel-content">
        {renderContent()}
      </div>

      {isLoading && (
        <div className="loading-overlay">
          <Loader2 className="spinner" size={40} />
          <span>Processing...</span>
        </div>
      )}
    </div>
  );
};

// ==================== 子视图组件 ====================

const MainView: React.FC<{
  session: AuthSession | null;
  identityCount: number;
  onCreate: () => void;
  onImport: () => void;
  onList: () => void;
  onScan: () => void;
  onSettings: () => void;
  onLogout: () => void;
}> = ({ session, identityCount, onCreate, onImport, onList, onScan, onSettings, onLogout }) => {
  return (
    <div className="main-view">
      {session ? (
        <div className="session-card active">
          <div className="session-status">
            <div className="status-indicator active"></div>
            <span>Authenticated</span>
          </div>
          <div className="session-did">
            <Fingerprint size={20} />
            <code>{session.did.substring(0, 30)}...</code>
          </div>
          <div className="session-meta">
            <span>Session expires: {new Date(session.expiresAt).toLocaleString()}</span>
          </div>
          <button className="btn-logout" onClick={onLogout}>
            <LogOut size={18} />
            Logout
          </button>
        </div>
      ) : (
        <div className="session-card">
          <div className="session-status">
            <div className="status-indicator inactive"></div>
            <span>Not Authenticated</span>
          </div>
          <p className="session-hint">Create or import a DID to get started</p>
        </div>
      )}

      <div className="action-grid">
        <ActionCard
          icon={<Plus size={28} />}
          title="Create Identity"
          description="Create a new decentralized identity"
          onClick={onCreate}
          primary
        />
        <ActionCard
          icon={<Upload size={28} />}
          title="Import Identity"
          description="Import an existing DID document"
          onClick={onImport}
        />
        <ActionCard
          icon={<Wallet size={28} />}
          title={`My Identities (${identityCount})`}
          description="Manage your DID identities"
          onClick={onList}
          disabled={identityCount === 0}
        />
        <ActionCard
          icon={<ScanLine size={28} />}
          title="Scan QR Code"
          description="Authenticate by scanning"
          onClick={onScan}
        />
        <ActionCard
          icon={<Settings size={28} />}
          title="Settings"
          description="Configure DID options"
          onClick={onSettings}
        />
        <ActionCard
          icon={<HelpCircle size={28} />}
          title="Help"
          description="Learn about DID authentication"
          onClick={() => window.open('https://w3c.github.io/did-core/', '_blank')}
        />
      </div>
    </div>
  );
};

const ActionCard: React.FC<{
  icon: React.ReactNode;
  title: string;
  description: string;
  onClick: () => void;
  primary?: boolean;
  disabled?: boolean;
}> = ({ icon, title, description, onClick, primary, disabled }) => (
  <motion.button
    whileHover={disabled ? {} : { scale: 1.02 }}
    whileTap={disabled ? {} : { scale: 0.98 }}
    className={`action-card ${primary ? 'primary' : ''} ${disabled ? 'disabled' : ''}`}
    onClick={onClick}
    disabled={disabled}
  >
    <div className="action-icon">{icon}</div>
    <div className="action-content">
      <h4>{title}</h4>
      <p>{description}</p>
    </div>
    <ChevronRight size={20} className="action-arrow" />
  </motion.button>
);

const CreateIdentityView: React.FC<{
  onCreated: () => void;
  onBack: () => void;
}> = ({ onCreated, onBack }) => {
  const [step, setStep] = useState<CreateStep>('method');
  const [method, setMethod] = useState<DIDMethod>(DIDMethod.ETHR);
  const [type, setType] = useState<IdentityType>(IdentityType.PERSONAL);
  const [metadata, setMetadata] = useState<Partial<IdentityMetadata>>({});
  const [isCreating, setIsCreating] = useState(false);

  const didService = DIDAuthService.getInstance();

  const handleCreate = async () => {
    setIsCreating(true);
    try {
      await didService.createIdentity(method, type, metadata);
      onCreated();
    } catch (error) {
      console.error('Failed to create identity:', error);
    } finally {
      setIsCreating(false);
    }
  };

  const renderStep = () => {
    switch (step) {
      case 'method':
        return (
          <div className="create-step">
            <h3>Select DID Method</h3>
            <div className="method-grid">
              {Object.values(DIDMethod).map((m) => (
                <MethodCard
                  key={m}
                  method={m}
                  selected={method === m}
                  onSelect={() => setMethod(m)}
                />
              ))}
            </div>
            <button className="btn-primary" onClick={() => setStep('type')}>
              Continue
            </button>
          </div>
        );
      case 'type':
        return (
          <div className="create-step">
            <h3>Select Identity Type</h3>
            <div className="type-grid">
              {Object.values(IdentityType).map((t) => (
                <TypeCard
                  key={t}
                  type={t}
                  selected={type === t}
                  onSelect={() => setType(t)}
                />
              ))}
            </div>
            <div className="step-actions">
              <button className="btn-secondary" onClick={() => setStep('method')}>
                Back
              </button>
              <button className="btn-primary" onClick={() => setStep('info')}>
                Continue
              </button>
            </div>
          </div>
        );
      case 'info':
        return (
          <div className="create-step">
            <h3>Identity Information (Optional)</h3>
            <div className="form-group">
              <label>Display Name</label>
              <input
                type="text"
                value={metadata.name || ''}
                onChange={(e) => setMetadata({ ...metadata, name: e.target.value })}
                placeholder="My Identity"
              />
            </div>
            <div className="form-group">
              <label>Description</label>
              <textarea
                value={metadata.description || ''}
                onChange={(e) => setMetadata({ ...metadata, description: e.target.value })}
                placeholder="Brief description..."
                rows={3}
              />
            </div>
            <div className="form-group">
              <label>Email</label>
              <input
                type="email"
                value={metadata.email || ''}
                onChange={(e) => setMetadata({ ...metadata, email: e.target.value })}
                placeholder="email@example.com"
              />
            </div>
            <div className="step-actions">
              <button className="btn-secondary" onClick={() => setStep('type')}>
                Back
              </button>
              <button className="btn-primary" onClick={() => setStep('keys')}>
                Continue
              </button>
            </div>
          </div>
        );
      case 'keys':
        return (
          <div className="create-step">
            <h3>Key Generation</h3>
            <div className="key-info">
              <Key size={48} />
              <p>A new key pair will be generated for your identity</p>
              <ul>
                <li>Ed25519 signature scheme</li>
                <li>Keys stored locally</li>
                <li>Never shared without permission</li>
              </ul>
            </div>
            <div className="step-actions">
              <button className="btn-secondary" onClick={() => setStep('info')}>
                Back
              </button>
              <button className="btn-primary" onClick={() => setStep('backup')}>
                Generate Keys
              </button>
            </div>
          </div>
        );
      case 'backup':
        return (
          <div className="create-step">
            <h3>Backup Your Identity</h3>
            <div className="backup-warning">
              <AlertTriangle size={32} />
              <p>Important: Store your recovery phrase safely. It cannot be recovered if lost.</p>
            </div>
            <div className="step-actions">
              <button className="btn-secondary" onClick={() => setStep('keys')}>
                Back
              </button>
              <button 
                className="btn-primary" 
                onClick={handleCreate}
                disabled={isCreating}
              >
                {isCreating ? 'Creating...' : 'Create Identity'}
              </button>
            </div>
          </div>
        );
    }
  };

  return (
    <div className="create-identity-view">
      <div className="step-indicator">
        {['method', 'type', 'info', 'keys', 'backup'].map((s, i) => (
          <div 
            key={s} 
            className={`step-dot ${step === s ? 'active' : ''} ${
              ['method', 'type', 'info', 'keys', 'backup'].indexOf(step) > i ? 'completed' : ''
            }`}
          >
            {['method', 'type', 'info', 'keys', 'backup'].indexOf(step) > i ? <CheckCircle size={14} /> : i + 1}
          </div>
        ))}
      </div>
      {renderStep()}
    </div>
  );
};

const MethodCard: React.FC<{
  method: DIDMethod;
  selected: boolean;
  onSelect: () => void;
}> = ({ method, selected, onSelect }) => {
  const methodInfo: Record<DIDMethod, { name: string; desc: string }> = {
    [DIDMethod.ETHR]: { name: 'ethr', desc: 'Ethereum-based DID' },
    [DIDMethod.WEB]: { name: 'web', desc: 'Web domain DID' },
    [DIDMethod.KEY]: { name: 'key', desc: 'Public key DID' },
    [DIDMethod.SOV]: { name: 'sov', desc: 'Sovrin DID' },
    [DIDMethod.ELEM]: { name: 'elem', desc: 'Element DID' },
    [DIDMethod.JOLIET]: { name: 'joliet', desc: 'Joliet DID' }
  };

  return (
    <div 
      className={`method-card ${selected ? 'selected' : ''}`}
      onClick={onSelect}
    >
      <div className="method-name">{methodInfo[method].name}</div>
      <div className="method-desc">{methodInfo[method].desc}</div>
      {selected && <CheckCircle className="check-icon" size={20} />}
    </div>
  );
};

const TypeCard: React.FC<{
  type: IdentityType;
  selected: boolean;
  onSelect: () => void;
}> = ({ type, selected, onSelect }) => {
  const typeIcons: Record<IdentityType, React.ReactNode> = {
    [IdentityType.PERSONAL]: <User size={24} />,
    [IdentityType.ENTERPRISE]: <Building size={24} />,
    [IdentityType.DEVICE]: <Smartphone size={24} />,
    [IdentityType.SERVICE]: <Globe size={24} />,
    [IdentityType.ANONYMOUS]: <Shield size={24} />
  };

  const typeLabels: Record<IdentityType, string> = {
    [IdentityType.PERSONAL]: 'Personal',
    [IdentityType.ENTERPRISE]: 'Enterprise',
    [IdentityType.DEVICE]: 'Device',
    [IdentityType.SERVICE]: 'Service',
    [IdentityType.ANONYMOUS]: 'Anonymous'
  };

  return (
    <div 
      className={`type-card ${selected ? 'selected' : ''}`}
      onClick={onSelect}
    >
      {typeIcons[type]}
      <span>{typeLabels[type]}</span>
      {selected && <CheckCircle className="check-icon" size={18} />}
    </div>
  );
};

const ImportIdentityView: React.FC<{
  onImported: () => void;
  onBack: () => void;
}> = ({ onImported, onBack }) => {
  const [jsonInput, setJsonInput] = useState('');
  const [isImporting, setIsImporting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const didService = DIDAuthService.getInstance();

  const handleImport = async () => {
    setIsImporting(true);
    setError(null);
    try {
      const document = JSON.parse(jsonInput);
      await didService.importIdentity(document);
      onImported();
    } catch (err: any) {
      setError(err.message || 'Invalid DID document');
    } finally {
      setIsImporting(false);
    }
  };

  return (
    <div className="import-identity-view">
      <h3>Import DID Identity</h3>
      <p className="import-hint">Paste your DID document JSON below</p>
      <textarea
        className="import-textarea"
        value={jsonInput}
        onChange={(e) => setJsonInput(e.target.value)}
        placeholder={`{\n  "@context": "https://www.w3.org/ns/did/v1",\n  "id": "did:ethr:mainnet:0x...",\n  ...\n}`}
        rows={15}
      />
      {error && (
        <div className="import-error">
          <AlertTriangle size={16} />
          <span>{error}</span>
        </div>
      )}
      <div className="import-actions">
        <button className="btn-secondary" onClick={onBack}>
          Cancel
        </button>
        <button 
          className="btn-primary" 
          onClick={handleImport}
          disabled={!jsonInput.trim() || isImporting}
        >
          {isImporting ? 'Importing...' : 'Import Identity'}
        </button>
      </div>
    </div>
  );
};

const IdentityListView: React.FC<{
  identities: DIDIdentity[];
  session: AuthSession | null;
  onSelect: (identity: DIDIdentity) => void;
  onAuthenticate: (did: string) => void;
  onDelete: (did: string) => void;
  onBack: () => void;
  isLoading: boolean;
}> = ({ identities, session, onSelect, onAuthenticate, onDelete, isLoading }) => {
  if (identities.length === 0) {
    return (
      <div className="empty-state">
        <Wallet size={48} />
        <h3>No Identities</h3>
        <p>Create or import an identity to get started</p>
      </div>
    );
  }

  return (
    <div className="identity-list-view">
      <div className="identity-list">
        {identities.map((identity) => (
          <IdentityCard
            key={identity.did}
            identity={identity}
            isActive={session?.did === identity.did}
            onSelect={onSelect}
            onDelete={onDelete}
          />
        ))}
      </div>
    </div>
  );
};

const IdentityCard: React.FC<IdentityCardProps> = ({
  identity,
  isActive,
  onSelect,
  onDelete,
  onExport
}) => {
  const [showMenu, setShowMenu] = useState(false);

  const statusIcons: Record<VerificationStatus, React.ReactNode> = {
    [VerificationStatus.VERIFIED]: <CheckCircle size={16} className="status-verified" />,
    [VerificationStatus.PENDING]: <Loader2 size={16} className="status-pending" />,
    [VerificationStatus.UNVERIFIED]: <Shield size={16} className="status-unverified" />,
    [VerificationStatus.EXPIRED]: <AlertTriangle size={16} className="status-expired" />,
    [VerificationStatus.REVOKED]: <XCircle size={16} className="status-revoked" />,
    [VerificationStatus.FAILED]: <XCircle size={16} className="status-failed" />
  };

  const typeIcons: Record<IdentityType, React.ReactNode> = {
    [IdentityType.PERSONAL]: <User size={18} />,
    [IdentityType.ENTERPRISE]: <Building size={18} />,
    [IdentityType.DEVICE]: <Smartphone size={18} />,
    [IdentityType.SERVICE]: <Globe size={18} />,
    [IdentityType.ANONYMOUS]: <Shield size={18} />
  };

  return (
    <motion.div
      whileHover={{ scale: 1.01 }}
      className={`identity-card ${isActive ? 'active' : ''}`}
      onClick={() => onSelect?.(identity)}
    >
      <div className="identity-icon">
        {typeIcons[identity.type]}
      </div>
      <div className="identity-info">
        <div className="identity-name">
          {identity.metadata.name || identity.type}
          {isActive && <span className="active-badge">Active</span>}
        </div>
        <div className="identity-did">
          <code>{identity.did.substring(0, 25)}...</code>
        </div>
        <div className="identity-meta">
          {statusIcons[identity.status]}
          <span>{identity.status}</span>
          <span className="separator">•</span>
          <span>{identity.method}</span>
          <span className="separator">•</span>
          <span>{new Date(identity.createdAt).toLocaleDateString()}</span>
        </div>
      </div>
      <div className="identity-actions">
        <button 
          className="btn-menu"
          onClick={(e) => {
            e.stopPropagation();
            setShowMenu(!showMenu);
          }}
        >
          <MoreHorizontal size={18} />
        </button>
        {showMenu && (
          <div className="dropdown-menu">
            <button onClick={() => { navigator.clipboard.writeText(identity.did); setShowMenu(false); }}>
              <Copy size={14} /> Copy DID
            </button>
            <button onClick={() => { onExport?.(identity.did); setShowMenu(false); }}>
              <Download size={14} /> Export
            </button>
            <button className="danger" onClick={() => { onDelete?.(identity.did); setShowMenu(false); }}>
              <Trash2 size={14} /> Delete
            </button>
          </div>
        )}
      </div>
    </motion.div>
  );
};

const IdentityDetailView: React.FC<{
  identity: DIDIdentity;
  session: AuthSession | null;
  onAuthenticate: () => void;
  onDelete: () => void;
  onBack: () => void;
  isLoading: boolean;
}> = ({ identity, session, onAuthenticate, onDelete, isLoading }) => {
  const [activeTab, setActiveTab] = useState<'overview' | 'credentials' | 'json'>('overview');
  const isAuthenticated = session?.did === identity.did;

  const copyToClipboard = (text: string) => {
    navigator.clipboard.writeText(text);
  };

  return (
    <div className="identity-detail-view">
      <div className="identity-header">
        <div className="identity-avatar">
          {identity.metadata.avatar ? (
            <img src={identity.metadata.avatar} alt="" />
          ) : (
            <Fingerprint size={40} />
          )}
        </div>
        <div className="identity-title">
          <h3>{identity.metadata.name || 'Unnamed Identity'}</h3>
          <span className={`status-badge ${identity.status}`}>{identity.status}</span>
        </div>
      </div>

      <div className="detail-tabs">
        <button 
          className={activeTab === 'overview' ? 'active' : ''}
          onClick={() => setActiveTab('overview')}
        >
          Overview
        </button>
        <button 
          className={activeTab === 'credentials' ? 'active' : ''}
          onClick={() => setActiveTab('credentials')}
        >
          Credentials ({identity.credentials.length})
        </button>
        <button 
          className={activeTab === 'json' ? 'active' : ''}
          onClick={() => setActiveTab('json')}
        >
          JSON
        </button>
      </div>

      <div className="detail-content">
        {activeTab === 'overview' && (
          <div className="overview-tab">
            <div className="detail-section">
              <h4>DID</h4>
              <div className="did-display">
                <code>{identity.did}</code>
                <button onClick={() => copyToClipboard(identity.did)}>
                  <Copy size={16} />
                </button>
              </div>
            </div>

            <div className="detail-grid">
              <div className="detail-item">
                <label>Method</label>
                <span>{identity.method}</span>
              </div>
              <div className="detail-item">
                <label>Type</label>
                <span>{identity.type}</span>
              </div>
              <div className="detail-item">
                <label>Created</label>
                <span>{new Date(identity.createdAt).toLocaleString()}</span>
              </div>
              <div className="detail-item">
                <label>Updated</label>
                <span>{new Date(identity.updatedAt).toLocaleString()}</span>
              </div>
            </div>

            {identity.metadata.description && (
              <div className="detail-section">
                <h4>Description</h4>
                <p>{identity.metadata.description}</p>
              </div>
            )}

            {identity.metadata.email && (
              <div className="detail-section">
                <h4>Contact</h4>
                <p>{identity.metadata.email}</p>
              </div>
            )}

            <div className="detail-actions">
              {!isAuthenticated ? (
                <button 
                  className="btn-primary btn-full"
                  onClick={onAuthenticate}
                  disabled={isLoading}
                >
                  {isLoading ? <Loader2 className="spin" size={18} /> : <Shield size={18} />}
                  Authenticate
                </button>
              ) : (
                <div className="auth-status">
                  <CheckCircle size={20} className="verified" />
                  <span>Authenticated</span>
                </div>
              )}
            </div>
          </div>
        )}

        {activeTab === 'credentials' && (
          <div className="credentials-tab">
            {identity.credentials.length === 0 ? (
              <div className="empty-credentials">
                <FileText size={32} />
                <p>No credentials yet</p>
              </div>
            ) : (
              identity.credentials.map((cred) => (
                <CredentialCard key={cred.id} credential={cred} />
              ))
            )}
          </div>
        )}

        {activeTab === 'json' && (
          <div className="json-tab">
            <pre>{JSON.stringify(identity.document, null, 2)}</pre>
          </div>
        )}
      </div>
    </div>
  );
};

const CredentialCard: React.FC<CredentialCardProps> = ({ credential, onVerify, onRemove }) => {
  const [expanded, setExpanded] = useState(false);

  return (
    <div className={`credential-card ${credential.status}`}>
      <div className="credential-header" onClick={() => setExpanded(!expanded)}>
        <div className="credential-type">{credential.type.join(', ')}</div>
        <div className="credential-status">
          {credential.status === 'verified' && <CheckCircle size={16} />}
          {credential.status === 'expired' && <AlertTriangle size={16} />}
          {credential.status === 'failed' && <XCircle size={16} />}
        </div>
      </div>
      {expanded && (
        <div className="credential-details">
          <div className="credential-issuer">
            <label>Issuer:</label>
            <code>{credential.issuer}</code>
          </div>
          <div className="credential-dates">
            <span>Issued: {new Date(credential.issuanceDate).toLocaleDateString()}</span>
            {credential.expirationDate && (
              <span>Expires: {new Date(credential.expirationDate).toLocaleDateString()}</span>
            )}
          </div>
          <div className="credential-actions">
            <button onClick={onVerify}>Verify</button>
            <button className="danger" onClick={onRemove}>Remove</button>
          </div>
        </div>
      )}
    </div>
  );
};

const ScanView: React.FC<{
  onAuthenticated: (session: AuthSession) => void;
  onBack: () => void;
}> = ({ onAuthenticated, onBack }) => {
  const [scanning, setScanning] = useState(true);
  const [sessionToken, setSessionToken] = useState('');

  const handleScan = async (data: string) => {
    setScanning(false);
    try {
      const didService = DIDAuthService.getInstance();
      const session = await didService.authenticateWithQR(data);
      onAuthenticated(session);
    } catch (error) {
      console.error('QR authentication failed:', error);
      setScanning(true);
    }
  };

  return (
    <div className="scan-view">
      {scanning ? (
        <div className="qr-scanner">
          <div className="scanner-frame">
            <ScanLine size={48} />
            <p>Scan QR code to authenticate</p>
          </div>
          <div className="scanner-hint">
            <p>Or enter session token manually:</p>
            <input
              type="text"
              value={sessionToken}
              onChange={(e) => setSessionToken(e.target.value)}
              placeholder="Session token..."
            />
            <button 
              onClick={() => handleScan(sessionToken)}
              disabled={!sessionToken}
            >
              Connect
            </button>
          </div>
        </div>
      ) : (
        <div className="authenticating">
          <Loader2 className="spin" size={32} />
          <p>Authenticating...</p>
        </div>
      )}
    </div>
  );
};

const SettingsView: React.FC<{
  onBack: () => void;
}> = ({ onBack }) => {
  const didService = DIDAuthService.getInstance();
  const [config, setConfig] = useState(didService.getConfig());

  return (
    <div className="settings-view">
      <h3>DID Settings</h3>
      <div className="settings-section">
        <h4>Resolver Configuration</h4>
        <div className="form-group">
          <label>Resolver URL</label>
          <input
            type="text"
            value={config.resolverUrl}
            onChange={(e) => {
              const newConfig = { ...config, resolverUrl: e.target.value };
              didService.updateConfig(newConfig);
              setConfig(newConfig);
            }}
          />
        </div>
      </div>
      <div className="settings-section">
        <h4>Cache Settings</h4>
        <div className="form-group checkbox">
          <label>
            <input
              type="checkbox"
              checked={config.cacheEnabled}
              onChange={(e) => {
                const newConfig = { ...config, cacheEnabled: e.target.checked };
                didService.updateConfig(newConfig);
                setConfig(newConfig);
              }}
            />
            Enable document caching
          </label>
        </div>
      </div>
      <div className="settings-actions">
        <button className="btn-secondary" onClick={() => didService.clearCache()}>
          Clear Cache
        </button>
      </div>
    </div>
  );
};

export default DIDAuthPanel;
