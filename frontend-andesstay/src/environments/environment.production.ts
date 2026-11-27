export const environment = {
  production: true,
  azureAd: {
    clientId: 'd6959b0c-9ec2-432e-bddb-54bd0e83ace9',
    tenantId: 'fbf645d5-8f6d-45fc-9e8d-c8866d008af1',
    authority: 'https://login.microsoftonline.com/fbf645d5-8f6d-45fc-9e8d-c8866d008af1',
    // Frontend publicado en AWS (API Gateway HTTP). No usar localhost en la entrega.
    redirectUri: 'https://e94n72mnmk.execute-api.us-east-1.amazonaws.com',
    postLogoutRedirectUri: 'https://e94n72mnmk.execute-api.us-east-1.amazonaws.com/login',
  },
  apiScope: 'api://d6959b0c-9ec2-432e-bddb-54bd0e83ace9/api.access',
  // Mismo API Gateway: sirve el frontend y hace de BFF de los microservicios.
  bffUrl: 'https://e94n72mnmk.execute-api.us-east-1.amazonaws.com',
};
