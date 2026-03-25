import {
  ZKPComputation,
  ZKPPrivacyProtection,
  CreateComputationRequest,
  GenerateProofRequest,
  VerifyPrivacyProtectionRequest,
  CreatePrivacyProtectionRequest,
  ApiResponse
} from '../types/zkpVerifiableComputation';

const API_BASE = '/api/zkp';

async function request<T>(url: string, options?: RequestInit): Promise<ApiResponse<T>> {
  try {
    const response = await fetch(url, {
      headers: { 'Content-Type': 'application/json' },
      ...options
    });
    const data = await response.json();
    return data as ApiResponse<T>;
  } catch (error) {
    return { success: false, error: error instanceof Error ? error.message : 'Unknown error' };
  }
}

export const zkpComputationService = {
  async createComputation(req: CreateComputationRequest): Promise<ApiResponse<{ computationId: string }>> {
    const params = new URLSearchParams({
      userId: req.userId,
      computationType: req.computationType,
      circuitType: req.circuitType || 'GROTH16',
      securityLevel: (req.securityLevel || 128).toString()
    });
    if (req.sessionId) params.append('sessionId', req.sessionId);
    
    return request(`${API_BASE}/computations/create`, {
      method: 'POST',
      body: JSON.stringify(params)
    });
  },

  async generateProof(computationId: string, req: GenerateProofRequest): Promise<ApiResponse<{ proofGenerated: boolean }>> {
    return request(`${API_BASE}/computations/${computationId}/generate-proof`, {
      method: 'POST',
      body: JSON.stringify(req)
    });
  },

  async verifyProof(computationId: string): Promise<ApiResponse<{ verified: boolean }>> {
    return request(`${API_BASE}/computations/${computationId}/verify`, { method: 'POST' });
  },

  async getComputation(computationId: string): Promise<ApiResponse<ZKPComputation>> {
    return request(`${API_BASE}/computations/${computationId}`);
  },

  async getUserComputations(userId: string, limit = 20): Promise<ApiResponse<ZKPComputation[]>> {
    return request(`${API_BASE}/users/${userId}/computations?limit=${limit}`);
  },

  async batchVerify(computationIds: string[]): Promise<ApiResponse<{ verified: number; failed: number }>> {
    return request(`${API_BASE}/computations/batch-verify`, {
      method: 'POST',
      body: JSON.stringify(computationIds)
    });
  }
};

export const zkpPrivacyService = {
  async createProtection(req: CreatePrivacyProtectionRequest): Promise<ApiResponse<{ protectionId: string }>> {
    return request(`${API_BASE}/privacy-protections/create`, {
      method: 'POST',
      body: JSON.stringify(req)
    });
  },

  async verifyProtection(protectionId: string, req: VerifyPrivacyProtectionRequest): Promise<ApiResponse<{ verified: boolean }>> {
    return request(`${API_BASE}/privacy-protections/${protectionId}/verify`, {
      method: 'POST',
      body: JSON.stringify(req)
    });
  },

  async getProtection(protectionId: string): Promise<ApiResponse<ZKPPrivacyProtection>> {
    return request(`${API_BASE}/privacy-protections/${protectionId}`);
  },

  async getUserProtections(userId: string, limit = 20): Promise<ApiResponse<ZKPPrivacyProtection[]>> {
    return request(`${API_BASE}/users/${userId}/privacy-protections?limit=${limit}`);
  }
};

export default { computations: zkpComputationService, privacy: zkpPrivacyService };
