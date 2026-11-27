// Igual que environment.ts, pero contra el backend desplegado en AWS. Se usa con
// `ng serve -c aws`: el frontend sigue corriendo en localhost:4200 (redirect URI de Azure AD)
// y solo cambia el destino de las llamadas al BFF.
export const environment = {
  production: false,
  azureAd: {
    clientId: 'e2d52786-8292-433e-a464-d575268eca38',
    tenantId: '5fb0afd3-b475-47e9-862b-1ce06143be35',
    authority: 'https://login.microsoftonline.com/5fb0afd3-b475-47e9-862b-1ce06143be35',
    redirectUri: 'http://localhost:4200',
    postLogoutRedirectUri: 'http://localhost:4200/login',
  },
  apiScope: 'api://e2d52786-8292-433e-a464-d575268eca38/read',
  // API Gateway HTTP de AWS usado en la entrega.
  bffUrl: 'https://a8dgq5xqm0.execute-api.us-east-1.amazonaws.com',
};
