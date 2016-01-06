"use strict";

import React from 'react';
import TokenView from './token-view.js';

class TokensBoard extends React.Component {
	/**
		props:
			tokens	[array]
	*/

	constructor(){
		super();
	}

	render(){
		var rows = [];
		var maxIndex = this.props.tokens.length-1;
		var cells = [];
		for(var i=0;i<=maxIndex;i++){
			cells.push(
					<TokenView model={this.props.tokens[i]} tokenSelectionCallback={this.props.tokenSelectionCallback} 
								isSelected = { this.props.selectedToken!=null && this.props.tokens[i]["id"] == this.props.selectedToken["id"]} />
			);
		}

		return (
			<div className="tokens-board">
				{cells}
			</div>	
		);
	}
}

export default TokensBoard