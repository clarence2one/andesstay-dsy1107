export const environment = {
  production: true,
  azureAd: {
    clientId: 'e2d52786-8292-433e-a464-d575268eca38',
    tenantId: '5fb0afd3-b475-47e9-862b-1ce06143be35',
    authority: 'https://login.microsoftonline.com/5fb0afd3-b475-47e9-862b-1ce06143be35',
    // Frontend publicado en AWS (API Gateway HTTP). No usar localhost en la entrega.
    redirectUri: 'https://e94n72mnmk.execute-api.us-east-1.amazonaws.com',
    postLogoutRedirectUri: 'https://e94n72mnmk.execute-api.us-east-1.amazonaws.com/login',
  },
  apiScope: 'api://e2d52786-8292-433e-a464-d575268eca38/read',
  // Mismo API Gateway: sirve el frontend y hace de BFF de los microservicios.
  bffUrl: 'https://e94n72mnmk.execute-api.us-east-1.amazonaws.com',
};
