import type { NextPage } from 'next';
import { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import Form from 'react-bootstrap/Form';

import { toastError, toastSuccess } from '@commonServices/ToastService';
import { INVENTORY_WAREHOUSE_STOCKS_HISTORIES_URL } from '@constants/Common';
import { ProductQuantityInStock } from '@inventoryModels/ProductQuantityInStock';
import { StockInfo } from '@inventoryModels/StockInfo';
import { Warehouse } from '@inventoryModels/Warehouse';
import {
  fetchStocksInWarehouseByProductNameAndProductSku,
  updateProductQuantityInStock,
} from '@inventoryServices/StockService';
import { getWarehouses } from '@inventoryServices/WarehouseService';

const WarehouseStocks: NextPage = () => {
  const [selectedWhId, setSelectedWhId] = useState<number>(0);
  const [warehouses, setWarehouses] = useState<Warehouse[]>([]);
  const [warehouseStocks, setWarehouseStocks] = useState<StockInfo[]>([]);
  const [productNameKw, setProductNameKw] = useState<string>('');
  const [productSkuKw, setProductSkuKw] = useState<string>('');
  const [productAdjustedQuantity, setProductAdjustedQuantity] = useState<Map<number, number>>(
    new Map()
  );
  const [productAdjustedNote, setProductAdjustedNote] = useState<Map<number, string>>(new Map());

  useEffect(() => {
    fetchWarehouses();
  }, []);

  useEffect(() => {
    if (selectedWhId) {
      fetchStocksInWarehouse();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedWhId, productNameKw, productSkuKw]);

  const fetchWarehouses = () => {
    getWarehouses()
      .then((results) => setWarehouses(results))
      .catch((error) => console.log(error));
  };

  const fetchStocksInWarehouse = () => {
    fetchStocksInWarehouseByProductNameAndProductSku(selectedWhId, productNameKw, productSkuKw)
      .then(async (result) => {
        if (result.status === 200) {
          let rs: StockInfo[] = await result.json();
          let productQuantityMap: Map<number, number> = new Map();
          setWarehouseStocks(rs);
          rs.forEach((stock) => productQuantityMap.set(stock.id, 0));
          setProductAdjustedQuantity(productQuantityMap);
        }
      })
      .catch(() => {
        toastError('Something wrong has just happened');
      });
  };

  const updateProductQuantityInStockOnClick = () => {
    let requestBody: ProductQuantityInStock[] = [];

    warehouseStocks.forEach((stock) => {
      if (productAdjustedQuantity.has(stock.id)) {
        requestBody.push({
          stockId: stock.id,
          quantity: productAdjustedQuantity.get(stock.id) ?? 0,
          note: productAdjustedNote.get(stock.id) ?? '',
        });
      }
    });

    updateProductQuantityInStock(requestBody)
      .then((rs) => {
        if (rs.ok) {
          updateProductQuantityInStockAfterSaved();
          toastSuccess('Stock quantity has been changed successfully');
        }
      })
      .catch(() => toastError('Something went wrong'));
  };

  const updateProductQuantityInStockAfterSaved = () => {
    let newStocks = [...warehouseStocks];
    let newMap = new Map();

    newStocks.forEach((stock) => {
      if (productAdjustedQuantity.has(stock.id)) {
        stock.quantity += productAdjustedQuantity.get(stock.id) ?? 0;
        productAdjustedQuantity.set(stock.id, 0);
      }
      newMap.set(stock.id, 0);
    });
    setProductAdjustedQuantity(newMap);
    setWarehouseStocks(newStocks);
  };

  const onChangeAdjustedQuantity = (event: any, stockId: number) => {
    let newMap = new Map(productAdjustedQuantity);
    newMap.set(stockId, Number(event.target.value));
    setProductAdjustedQuantity(newMap);
  };

  const onChangeAdjustedNote = (event: any, stockId: number) => {
    let newMap = new Map(productAdjustedNote);
    newMap.set(stockId, event.target.value);
    setProductAdjustedNote(newMap);
  };

  return (
    <>
      <div className="row mt-5">
        <div className="col-md-5">
          <h2 className="text-danger font-weight-bold mb-3">Manage Warehouse Stocks</h2>
        </div>
      </div>

      <div>
        <Form>
          <div className="row col-md-12">
            <div className="col-md-6">
              <Form.Select
                id="warehouse-selection"
                onChange={(e) => {
                  setSelectedWhId(Number(e.target.value));
                }}
                style={!selectedWhId ? { color: '#838d8d' } : {}}
                placeholder="Select Warehouse..."
              >
                <option key="all" style={{ color: '#838d8d' }} value={undefined}>
                  Select Warehouse...
                </option>
                {warehouses.map((item) => (
                  <option key={item.id} style={{ color: 'black' }} value={item.id}>
                    {item.name}
                  </option>
                ))}
              </Form.Select>
            </div>
          </div>
          <div className="row col-md-12 mt-3">
            <div className="col-md">
              <Form.Control
                id="product-name"
                placeholder="Search product name ..."
                defaultValue={productNameKw}
                onChange={(event) => setProductNameKw(event.target.value)}
                disabled={!selectedWhId}
              />
            </div>
            <div className="col-md">
              <Form.Control
                id="product-sku"
                placeholder="Search product SKU ..."
                defaultValue={productSkuKw}
                onChange={(event) => setProductSkuKw(event.target.value)}
                disabled={!selectedWhId}
              />
            </div>
          </div>
        </Form>
      </div>
      <div className="mt-3">
        <Table striped bordered hover className="mt-2">
          <thead>
            <tr>
              <th>Name</th>
              <th>SKU</th>
              <th>Current Quantity</th>
              <th>(+/-)Adjusted Quantity</th>
              <th>Note</th>
              <th>Stock History</th>
            </tr>
          </thead>
          <tbody>
            {warehouseStocks.map((stockInfo, index) => (
              <tr key={stockInfo.id}>
                <td>{stockInfo.productName}</td>
                <td>{stockInfo.productSku}</td>
                <td>{stockInfo.quantity}</td>
                <td>
                  <form>
                    <Form.Control
                      type="number"
                      id="product-adjusted-quantity"
                      placeholder="Adjusted quantity"
                      defaultValue={0}
                      onChange={(event) => onChangeAdjustedQuantity(event, stockInfo.id)}
                    />
                  </form>
                </td>
                <td>
                  <form>
                    <Form.Control
                      id="product-adjusted-note"
                      placeholder="Adjusted note"
                      onChange={(event) => {
                        onChangeAdjustedNote(event, stockInfo.id);
                      }}
                    />
                  </form>
                </td>
                <td>
                  <div style={{ display: 'flex', justifyContent: 'center' }}>
                    <a
                      href={`${INVENTORY_WAREHOUSE_STOCKS_HISTORIES_URL}?warehouseId=${stockInfo.warehouseId}&productId=${stockInfo.productId}`}
                    >
                      View History
                    </a>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </Table>
        <div style={{ display: 'flex', justifyContent: 'end' }}>
          <Button variant="primary" onClick={updateProductQuantityInStockOnClick}>
            Save
          </Button>
        </div>
      </div>
    </>
  );
};

export default WarehouseStocks;
