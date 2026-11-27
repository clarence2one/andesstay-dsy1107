export const environment = {
  production: true,
  azureAd: {
    clientId: 'e2d52786-8292-433e-a464-d575268eca38',
    tenantId: '5fb0afd3-b475-47e9-862b-1ce06143be35',
    authority: 'https://login.microsoftonline.com/5fb0afd3-b475-47e9-862b-1ce06143be35',
    // Se resuelve solo a la URL publica donde quede desplegado el frontend en AWS.
    redirectUri: window.location.origin,
    postLogoutRedirectUri: window.location.origin,
  },
  apiScope: 'api://e2d52786-8292-433e-a464-d575268eca38/read',
  // El frontend y el BFF viven en el mismo origen del API Gateway de AWS.
  bffUrl: window.location.origin,
};
