export const environment = {
  production: false,
  azureAd: {
    clientId: 'e2d52786-8292-433e-a464-d575268eca38',
    tenantId: '5fb0afd3-b475-47e9-862b-1ce06143be35',
    authority: 'https://login.microsoftonline.com/5fb0afd3-b475-47e9-862b-1ce06143be35',
    redirectUri: 'http://localhost:4200',
    postLogoutRedirectUri: 'http://localhost:4200/login',
  },
  // Scope expuesto por el App Registration ("Expose an API"): api://<clientId>/read
  apiScope: 'api://e2d52786-8292-433e-a464-d575268eca38/read',
  // BFF local (ms-andesstay-bff). Para probar contra AWS: `ng serve -c aws` (environment.aws.ts).
  bffUrl: 'http://localhost:8081',
};
