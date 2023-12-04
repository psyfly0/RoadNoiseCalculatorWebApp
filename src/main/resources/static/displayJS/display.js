 // Define mapping of user-friendly column names to actual column names
		    const columnMapping = {
		        'Azonosító': 'identifier',
		        'I_km/h': 'kmh1',
		        'II_km/h': 'kmh2',
		        'III_km/h': 'kmh3',
		        'I_ak_kat_N': 'acCatDay1',
		        'II_ak_kat_N': 'acCatDay2',
		        'III_ak_kat_N': 'acCatDay3',
		        'I_ak_kat_E': 'acCatNight1',
		        'II_ak_kat_E': 'acCatNight2',
		        'III_ak_kat_E': 'acCatNight3',
		        'R_Azonosító': 'reverseIdentifier',
		        'R_I_km/h': 'reverseKmh1',
		        'R_II_km/h': 'reverseKmh2',
		        'R_III_km/h': 'reverseKmh3',
		        'R_I_ak_kat_N': 'reverseAcCatDay1',
		        'R_II_ak_kat_N': 'reverseAcCatDay2',
		        'R_III_ak_kat_N': 'reverseAcCatDay3',
		        'R_I_ak_kat_E': 'reverseAcCatNight1',
		        'R_II_ak_kat_E': 'reverseAcCatNight2',
		        'R_III_ak_kat_E': 'reverseAcCatNight3',
		        'proba' : 'valami'
		    };
		    
		    
		    
		    // listener for calculations div
		    function handleCalculationChange() {
			    const selectElement = document.getElementById('calculations');
			    const selectedValue = selectElement.value;
			
			    // Perform actions based on the selected option value (selectedValue)
			    if (selectedValue !== '' && activeFileId) {
					console.log('active field:', activeFileId);
			        fetch(`${selectedValue}/${activeFileId}`, {
			            method: 'POST',
			            headers: {
			                'Content-Type': 'application/json'
			            },
			            body: JSON.stringify({ fileId: activeFileId })
			        })
			        .then(response => {
			            if (!response.ok) {
			                throw new Error('Network response was not ok');
			            }
			            // Handle the response as needed
			        })
			        .catch(error => {
			            console.error('There was a problem with the fetch operation:', error);
			        });
			    }
			}

		
		       
			// Function to fetch data, group by file_id, and render tabs and tables
			const fetchDataAndRenderTabs = () => {
			    fetch('/console/displayData')
			        .then(response => {
			            if (!response.ok) {
			                throw new Error('Network response was not ok');
			            }
			            return response.json();
			        })
			        .then(data => {
			            console.log('Fetched Data:', data);
			            renderTabsAndTables(data);
			        })
			        .catch(error => {
			            console.error('There was a problem with the fetch operation:', error);
			        });
			};
			
			let activeFileId;
			// Function to render tabs and tables using grouped data
			const renderTabsAndTables = (groupedData) => {
			    console.log('Grouped Data:', groupedData);
			        const tabs = Object.keys(groupedData).map(fileId => {
			        const fileName = groupedData[fileId][0].fileName;
			
			        return (
			            <span key={fileId}>
			                <button onClick={() => {
			                    renderTable(groupedData[fileId]);
			                    activeFileId = fileId; // Set the active file_id
			                }}>
			                    {fileName}
			                </button>
			            </span>
			        );
			    });
			
			    ReactDOM.render(
			        <div>
			            {tabs}
			            <div id="table-container"></div>
			        </div>,
			        document.getElementById('root')
			    );
			
			    // Display the table for the lowest file_id by default
			    const lowestFileId = Math.min(...Object.keys(groupedData));
			    renderTable(groupedData[lowestFileId]);
			};
			
			// Function to render a specific table for a file
			const renderTable = (data) => {
			    console.log('Data for Table:', data);
			    
			    // Extracting the fileName from the data
    			const fileName = data.length > 0 ? data[0].fileName : '';
    			
    			// Displaying the table name (fileName) above the table
				const tableName = <h2>{fileName}</h2>;
			    
			    // Extract unique column keys from the data
			    const allColumnKeys = data.reduce((keys, dataItem) => {
			        return [...keys, ...Object.keys(dataItem)];
			    }, []);
			
			    const uniqueColumnKeys = [...new Set(allColumnKeys)];
			
			    const columnsToDisplay = Object.entries(columnMapping)
			        .filter(([displayName, columnKey]) => {
			            // Check if the column exists in the data
			            return uniqueColumnKeys.includes(columnKey);
			        })
			        .map(([displayName, columnKey]) => {
			            return { displayName, columnKey };
			        });
			
			    const tableHeaders = columnsToDisplay.map(({ displayName }, index) => (
			        <th key={index}>{displayName}</th>
			    ));
			
			    const tableRows = data.map((dataItem, index) => {
			        return (
			            <tr key={index}>
			                {columnsToDisplay.map(({ columnKey }, idx) => {
			                    const cellValue = dataItem[columnKey];
			                    return <td key={idx}>{cellValue}</td>;
			                })}
			            </tr>
			        );
			    });
			
			    const table = (
					<div>
						{tableName}
				        <table>
				            <thead>
				                <tr>
				                    {tableHeaders}
				                </tr>
				            </thead>
				            <tbody>
				                {tableRows}
				            </tbody>
				        </table>
			        </div>
			    );
			    console.log('Rendering Table:', table);
			    ReactDOM.render(table, document.getElementById('table-container'));
			};
			
			// Call the function to fetch data and render tabs on page load
			fetchDataAndRenderTabs();