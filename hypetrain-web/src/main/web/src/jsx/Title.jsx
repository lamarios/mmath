import React from 'react';
import styled from 'styled-components';

import { NavLink } from 'react-router-dom';

const Div = styled(NavLink)`
  grid-area: title;
  text-align: center;
  margin-bottom:20px;
  text-decoration: none;
`;


const Main = styled.div`
  font-size: 40px;
  color: ${props => props.theme.colors.accent};
`;

const Sub = styled.div`
  color: ${props => props.theme.colors.text};
  font-size: 20px;
`;

export default class Title extends React.Component {


    render() {
        return (
            <Div to={"/"}>
                <Main>MMA Hype Train</Main>
                <Sub>"Tchoo Tchoo"</Sub>
            </Div>
        );
    }
}