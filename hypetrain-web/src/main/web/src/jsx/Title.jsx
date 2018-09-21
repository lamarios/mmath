import React from 'react';
import styled from 'styled-components';


const Div = styled.div`
  grid-area: title;
  text-align: center;
  margin-bottom:20px;
`;


const Main = styled.div`
  font-size: 40px;
  color: ${props => props.theme.colors.accent};
`;

const Sub = styled.div`
  color: ${props => props.theme.colors.main};
  font-size: 20px;
`;

export default class Title extends React.Component {


    render() {
        return (
            <Div>
                <Main>Mma Hype Train</Main>
                <Sub>"Tchoo Tchoo"</Sub>
            </Div>
        );
    }
}