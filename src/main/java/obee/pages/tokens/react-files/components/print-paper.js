"use strict";

import React from 'react';
import PrintToken from './print-token.js';

class PrintPaper extends React.Component {
	/**
		props:
			model					[Object]
			tokenDeletionCallback	function
	*/

	constructor(){
		super()
	}

	render(){
		var rows = [[],[],[]];
		for (var i=0; i<this.props.model.length; i++){
			var m = this.props.model[i];
			rows[parseInt(i/6)].push(<td><PrintToken onDoubleClickCallback={this.props.tokenDeletionCallback} model={m} /></td>);
		}
		return (
			<div className="print-paper">
				<table>
					<tr>	
						{rows[0]}
					</tr>
					<tr>	
						{rows[1]}
					</tr>
					<tr>	
						{rows[2]}
					</tr>
				</table>
			</div>	
		);
	}
}

export default PrintPaper