import React from 'react';
import { ProLayout } from '@ant-design/pro-components';
import { Outlet, useNavigate } from 'react-router-dom';
import { App as AntApp } from 'antd';
import { DashboardOutlined, UserOutlined } from '@ant-design/icons';

const menuItems = [
  { path: '/dashboard', name: 'Dashboard', icon: <DashboardOutlined /> },
  { path: '/users', name: 'Users', icon: <UserOutlined /> },
];

const ProShell: React.FC = () => {
  const navigate = useNavigate();
  return (
    <AntApp>
      <ProLayout
        title="Apex IDP"
        route={{ routes: menuItems }}
        onMenuHeaderClick={() => navigate('/dashboard')}
        menuItemRender={(item, dom) => (
          <a onClick={() => item.path && navigate(item.path)}>{dom}</a>
        )}
      >
        <Outlet />
      </ProLayout>
    </AntApp>
  );
};

export default ProShell;
