import React from 'react';
import styled from 'styled-components';


const Div = styled.div`
  grid-area: title;
`
export default class Title extends React.Component {


    render() {
        return (
            <Div>
                Mmath Hype Train
            </Div>
        );
    }
}