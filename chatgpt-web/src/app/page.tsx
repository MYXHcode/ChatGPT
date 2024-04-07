import {NextPage} from 'next';
import dynamic from 'next/dynamic';

// @ts-ignore
const AdminApp = dynamic(() => import('@/app/components/admin/AdminApp'), {
    ssr: false,
});

const Admin: NextPage = () => <AdminApp/>;

export default Admin;
