import React, { useState } from 'react';
import { ProTable } from '@ant-design/pro-components';
import { Drawer, Descriptions } from 'antd';

interface User {
  key: number;
  name: string;
  email: string;
  status: string;
  groups: string[];
  mfa: boolean;
  lastLogin: string;
}

const usersData: User[] = [
  {
    key: 1,
    name: 'John Doe',
    email: 'john@example.com',
    status: 'Active',
    groups: ['Admins'],
    mfa: true,
    lastLogin: '2025-07-30',
  },
  {
    key: 2,
    name: 'Jane Smith',
    email: 'jane@example.com',
    status: 'Inactive',
    groups: ['Users'],
    mfa: false,
    lastLogin: '2025-07-28',
  },
];

const Users: React.FC = () => {
  const [drawerVisible, setDrawerVisible] = useState(false);
  const [selectedUser, setSelectedUser] = useState<User | null>(null);

  const columns = [
    {
      title: 'Name',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: 'Email',
      dataIndex: 'email',
      key: 'email',
    },
    {
      title: 'Status',
      dataIndex: 'status',
      key: 'status',
    },
    {
      title: 'Groups',
      dataIndex: 'groups',
      key: 'groups',
      render: (groups: string[]) => groups.join(', '),
    },
    {
      title: 'MFA',
      dataIndex: 'mfa',
      key: 'mfa',
      render: (mfa: boolean) => (mfa ? 'Yes' : 'No'),
    },
    {
      title: 'Last Login',
      dataIndex: 'lastLogin',
      key: 'lastLogin',
    },
    {
      title: 'Action',
      key: 'action',
      render: (_: any, record: User) => (
        <a
          onClick={() => {
            setSelectedUser(record);
            setDrawerVisible(true);
          }}
        >
          View
        </a>
      ),
    },
  ];

  return (
    <>
      <ProTable<User>
        columns={columns as any}
        dataSource={usersData}
        rowKey="key"
        pagination={{ pageSize: 5 }}
        search={false as any}
        toolBarRender={false}
      />
      <Drawer
        title="User Details"
        width={400}
        onClose={() => setDrawerVisible(false)}
        open={drawerVisible}
      >
        {selectedUser && (
          <Descriptions column={1} layout="vertical">
            <Descriptions.Item label="Name">
              {selectedUser.name}
            </Descriptions.Item>
            <Descriptions.Item label="Email">
              {selectedUser.email}
            </Descriptions.Item>
            <Descriptions.Item label="Status">
              {selectedUser.status}
            </Descriptions.Item>
            <Descriptions.Item label="Groups">
              {selectedUser.groups.join(', ')}
            </Descriptions.Item>
            <Descriptions.Item label="MFA">
              {selectedUser.mfa ? 'Enabled' : 'Disabled'}
            </Descriptions.Item>
            <Descriptions.Item label="Last Login">
              {selectedUser.lastLogin}
            </Descriptions.Item>
          </Descriptions>
        )}
      </Drawer>
    </>
  );
};

export default Users;
