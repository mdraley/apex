import React from 'react';
import { ProCard } from '@ant-design/pro-components';
import { Row, Col, Statistic } from 'antd';
import { Line } from '@ant-design/charts';

const data = [
  { date: '2025-07-01', value: 120 },
  { date: '2025-07-08', value: 200 },
  { date: '2025-07-15', value: 260 },
  { date: '2025-07-22', value: 240 },
  { date: '2025-07-29', value: 320 },
];

const Dashboard: React.FC = () => {
  const config = {
    data,
    xField: 'date',
    yField: 'value',
    smooth: true,
    autoFit: true,
  };

  return (
    <ProCard direction="column" gutter={[16, 16]}>
      <Row gutter={16}>
        <Col span={6}>
          <ProCard>
            <Statistic title="Total Users" value={18432} />
          </ProCard>
        </Col>
        <Col span={6}>
          <ProCard>
            <Statistic title="Active (30d)" value={12108} />
          </ProCard>
        </Col>
        <Col span={6}>
          <ProCard>
            <Statistic title="MFA Enrolled" value={15990} />
          </ProCard>
        </Col>
        <Col span={6}>
          <ProCard>
            <Statistic title="SSO Apps" value={86} />
          </ProCard>
        </Col>
      </Row>
      <ProCard title="User Activity">
        {/* eslint-disable-next-line react/jsx-props-no-spreading */}
        <Line {...config} />
      </ProCard>
    </ProCard>
  );
};

export default Dashboard;
