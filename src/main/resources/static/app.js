const statusEl = document.getElementById('status');
const loggedOutEl = document.getElementById('logged-out');
const loggedInEl = document.getElementById('logged-in');
const loginBtn = document.getElementById('login');
const signupBtn = document.getElementById('signup');
const loginEmailBtn = document.getElementById('login-email');
const logoutBtn = document.getElementById('logout');
const callApiBtn = document.getElementById('call-api');
const nameEl = document.getElementById('user-name');
const emailEl = document.getElementById('user-email');
const avatarEl = document.getElementById('avatar');
const resultEl = document.getElementById('result');

let auth0Client;

async function init() {
  const { createAuth0Client } = window.auth0 || {};
  if (!createAuth0Client) {
    throw new Error('Failed to load the Auth0 SDK');
  }

  const config = await fetch('/api/config').then((response) => {
    if (!response.ok) {
      throw new Error('Unable to load Auth0 configuration');
    }
    return response.json();
  });

  auth0Client = await createAuth0Client({
    domain: config.domain,
    clientId: config.clientId,
    authorizationParams: {
      redirect_uri: window.location.origin,
      audience: config.audience,
      scope: 'openid profile email'
    }
  });

  const query = new URLSearchParams(window.location.search);
  if ((query.has('code') || query.has('error')) && query.has('state')) {
    await auth0Client.handleRedirectCallback();
    window.history.replaceState({}, document.title, window.location.pathname);
  }

  await render();
}

async function render() {
  const authenticated = await auth0Client.isAuthenticated();

  loggedOutEl.hidden = authenticated;
  loggedInEl.hidden = !authenticated;
  statusEl.textContent = authenticated ? 'Signed in' : 'Not signed in';

  if (authenticated) {
    const user = await auth0Client.getUser();
    nameEl.textContent = user.name || user.nickname || '';
    emailEl.textContent = user.email || '';
    avatarEl.src = user.picture || '';
    avatarEl.hidden = !user.picture;
  } else {
    resultEl.hidden = true;
    resultEl.textContent = '';
  }
}

async function callProtectedApi() {
  const token = await auth0Client.getTokenSilently();
  const response = await fetch('/api/auth/me', {
    headers: { Authorization: `Bearer ${token}` }
  });

  resultEl.hidden = false;
  resultEl.textContent = JSON.stringify(await response.json(), null, 2);
}

loginBtn.addEventListener('click', () => auth0Client.loginWithRedirect());

signupBtn.addEventListener('click', () => {
  auth0Client.loginWithRedirect({
    authorizationParams: { screen_hint: 'signup' }
  });
});

loginEmailBtn.addEventListener('click', () => {
  auth0Client.loginWithRedirect({
    authorizationParams: { connection: 'email' }
  });
});

logoutBtn.addEventListener('click', () => {
  auth0Client.logout({ logoutParams: { returnTo: window.location.origin } });
});

callApiBtn.addEventListener('click', () => {
  callProtectedApi().catch((error) => {
    resultEl.hidden = false;
    resultEl.textContent = error.message;
  });
});

init().catch((error) => {
  statusEl.textContent = error.message;
});
