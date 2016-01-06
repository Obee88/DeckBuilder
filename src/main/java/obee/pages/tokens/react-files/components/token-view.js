"use strict";

import React from 'react';

class TokensView extends React.Component {
	/**
		props:
			model					[Object]
			tokenSelectionCallback 	function	
	*/

	constructor(){
		super()
	}

	handleDblClick(){
		this.props.tokenSelectionCallback(this.props.model);
	}

	render(){
		var className = "token-view";
		if (this.props.isSelected){
			className = className + " selected-token";
		}
		return (
			<div className={className} onDoubleClick={this.handleDblClick.bind(this)} >
				<img style={{width:"223px", height:"311px"}} src={this.props.model.url} />
			</div>	
		);
	}
}

export default TokensView