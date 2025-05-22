import apiClientService from '@commonServices/ApiClientService';
import Link from 'next/link';
import { useEffect, useState } from 'react';

const baseUrl = '/authentication/user';

export default function AuthenticationInfo() {
  type AuthenticatedUser = {
    username: string;
  };

  const [authenticatedUser, setAuthenticatedUser] = useState<AuthenticatedUser>({ username: '' });

  async function getAuthenticatedUser() {
    return (await apiClientService.get(baseUrl)).json();
  }

  useEffect(() => {
    getAuthenticatedUser().then((data) => {
      setAuthenticatedUser(data);
    });
  }, []);

  return (
    <>
      Signed in as: <Link href="/profile">{authenticatedUser.username}</Link>{' '}
      <Link href="/logout">Logout</Link>
    </>
  );
}
