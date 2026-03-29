import 'dart:convert';
import 'package:flutter/material.dart';
import '../../models/live/live_product_model.dart';
import '../../models/live/live_commerce_service.dart';
import '../../services/live/live_commerce_service.dart';

/// 直播订单确认页面
class LiveOrderConfirmPage extends StatefulWidget {
  final Map<String, dynamic>? arguments;

  const LiveOrderConfirmPage({
    Key? key,
    this.arguments,
  }) : super(key: key);

  @override
  State<LiveOrderConfirmPage> createState() => _LiveOrderConfirmPageState();
}

class _LiveOrderConfirmPageState extends State<LiveOrderConfirmPage> {
  final LiveCommerceService _commerceService = LiveCommerceService();
  
  String? _roomId;
  LiveProductModel? _product;
  int? _quantity;
  List<CartItem>? _cartItems;
  
  // 地址信息
  final TextEditingController _nameController = TextEditingController();
  final TextEditingController _phoneController = TextEditingController();
  final TextEditingController _addressController = TextEditingController();
  
  // 配送方式
  String _deliveryType = 'express'; // express, self_pickup, same_day
  String? _selectedPickupPointId;
  List<PickupPoint> _pickupPoints = [];
  
  // 优惠券
  LiveCoupon? _selectedCoupon;
  List<LiveCoupon> _availableCoupons = [];
  
  // 备注
  final TextEditingController _remarkController = TextEditingController();
  
  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    _parseArguments();
    _loadPickupPoints();
    _loadCoupons();
  }

  void _parseArguments() {
    final args = widget.arguments;
    if (args != null) {
      _roomId = args['roomId'] as String?;
      _product = args['product'] as LiveProductModel?;
      _quantity = args['quantity'] as int? ?? 1;
      _cartItems = args['cartItems'] as List<CartItem>?;
    }
  }

  Future<void> _loadPickupPoints() async {
    if (_roomId == null) return;
    
    final points = await _commerceService.getPickupPoints(_roomId!);
    setState(() {
      _pickupPoints = points;
      if (points.isNotEmpty) {
        _selectedPickupPointId = points.first.id;
      }
    });
  }

  Future<void> _loadCoupons() async {
    if (_roomId == null) return;
    
    final coupons = await _commerceService.getAvailableCoupons(_roomId!);
    setState(() {
      _availableCoupons = coupons;
    });
  }

  /// 计算商品总额
  double get _productTotal {
    if (_cartItems != null) {
      return _cartItems!.fold(0, (sum, item) => sum + item.subtotal);
    } else if (_product != null && _quantity != null) {
      return _product!.price * _quantity!;
    }
    return 0.0;
  }

  /// 计算优惠金额
  double get _discountAmount {
    if (_selectedCoupon != null) {
      return _selectedCoupon!.calculateDiscount(_productTotal);
    }
    return 0.0;
  }

  /// 计算应付金额
  double get _payableAmount {
    return _productTotal - _discountAmount;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.grey[100],
      appBar: AppBar(
        title: Text('确认订单'),
        elevation: 0,
      ),
      body: _isLoading
          ? Center(child: CircularProgressIndicator())
          : Column(
              children: [
                Expanded(
                  child: SingleChildScrollView(
                    child: Column(
                      children: [
                        // 收货地址
                        _buildAddressSection(),
                        
                        // 配送方式
                        _buildDeliverySection(),
                        
                        // 商品信息
                        _buildProductSection(),
                        
                        // 优惠券
                        _buildCouponSection(),
                        
                        // 备注
                        _buildRemarkSection(),
                        
                        // 价格明细
                        _buildPriceSection(),
                      ],
                    ),
                  ),
                ),
                
                // 底部结算栏
                _buildBottomBar(),
              ],
            ),
    );
  }

  /// 构建地址区域
  Widget _buildAddressSection() {
    return Container(
      margin: EdgeInsets.all(12),
      padding: EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(8),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            '收货信息',
            style: TextStyle(fontSize: 14, fontWeight: FontWeight.bold),
          ),
          SizedBox(height: 12),
          TextField(
            controller: _nameController,
            decoration: InputDecoration(
              labelText: '收件人姓名',
              border: OutlineInputBorder(),
              contentPadding: EdgeInsets.symmetric(horizontal: 12, vertical: 12),
            ),
          ),
          SizedBox(height: 12),
          TextField(
            controller: _phoneController,
            decoration: InputDecoration(
              labelText: '联系电话',
              border: OutlineInputBorder(),
              contentPadding: EdgeInsets.symmetric(horizontal: 12, vertical: 12),
            ),
            keyboardType: TextInputType.phone,
          ),
          SizedBox(height: 12),
          TextField(
            controller: _addressController,
            decoration: InputDecoration(
              labelText: '详细地址',
              border: OutlineInputBorder(),
              contentPadding: EdgeInsets.symmetric(horizontal: 12, vertical: 12),
            ),
            maxLines: 2,
          ),
        ],
      ),
    );
  }

  /// 构建配送方式区域
  Widget _buildDeliverySection() {
    return Container(
      margin: EdgeInsets.symmetric(horizontal: 12),
      padding: EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(8),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            '配送方式',
            style: TextStyle(fontSize: 14, fontWeight: FontWeight.bold),
          ),
          SizedBox(height: 12),
          Row(
            children: [
              _buildDeliveryOption('express', '快递配送', Icons.local_shipping),
              SizedBox(width: 12),
              _buildDeliveryOption('self_pickup', '到店自提', Icons.store),
              SizedBox(width: 12),
              _buildDeliveryOption('same_day', '同城配送', Icons.delivery_dining),
            ],
          ),
          if (_deliveryType == 'self_pickup' && _pickupPoints.isNotEmpty)
            Container(
              margin: EdgeInsets.only(top: 12),
              padding: EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: Colors.grey[100],
                borderRadius: BorderRadius.circular(8),
              ),
              child: Column(
                children: _pickupPoints.map((point) {
                  return RadioListTile<String>(
                    title: Text(point.name),
                    subtitle: Text(point.address),
                    value: point.id,
                    groupValue: _selectedPickupPointId,
                    onChanged: (value) {
                      setState(() => _selectedPickupPointId = value);
                    },
                    dense: true,
                    contentPadding: EdgeInsets.zero,
                  );
                }).toList(),
              ),
            ),
        ],
      ),
    );
  }

  /// 构建配送选项
  Widget _buildDeliveryOption(String value, String label, IconData icon) {
    final isSelected = _deliveryType == value;
    
    return Expanded(
      child: GestureDetector(
        onTap: () => setState(() => _deliveryType = value),
        child: Container(
          padding: EdgeInsets.symmetric(vertical: 12),
          decoration: BoxDecoration(
            color: isSelected ? Colors.pink.withOpacity(0.1) : Colors.grey[100],
            borderRadius: BorderRadius.circular(8),
            border: Border.all(
              color: isSelected ? Colors.pink : Colors.transparent,
            ),
          ),
          child: Column(
            children: [
              Icon(icon, color: isSelected ? Colors.pink : Colors.grey),
              SizedBox(height: 4),
              Text(
                label,
                style: TextStyle(
                  fontSize: 12,
                  color: isSelected ? Colors.pink : Colors.black87,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  /// 构建商品区域
  Widget _buildProductSection() {
    final items = _cartItems ?? [];
    
    if (items.isEmpty && _product != null) {
      // 快速购买模式
      return Container(
        margin: EdgeInsets.all(12),
        padding: EdgeInsets.all(16),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(8),
        ),
        child: Row(
          children: [
            ClipRRect(
              borderRadius: BorderRadius.circular(4),
              child: _product!.images?.isNotEmpty == true
                  ? Image.network(
                      _product!.images!.first,
                      width: 80,
                      height: 80,
                      fit: BoxFit.cover,
                    )
                  : Container(
                      width: 80,
                      height: 80,
                      color: Colors.grey[300],
                    ),
            ),
            SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    _product!.name,
                    style: TextStyle(fontWeight: FontWeight.bold),
                  ),
                  SizedBox(height: 4),
                  Text(
                    '¥${_product!.price} x $_quantity',
                    style: TextStyle(color: Colors.grey[600]),
                  ),
                ],
              ),
            ),
            Text(
              '¥${(_product!.price * _quantity!).toStringAsFixed(2)}',
              style: TextStyle(
                fontWeight: FontWeight.bold,
                color: Colors.red,
              ),
            ),
          ],
        ),
      );
    }

    // 购物车模式
    return Container(
      margin: EdgeInsets.symmetric(horizontal: 12),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(8),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Padding(
            padding: EdgeInsets.all(16),
            child: Text(
              '商品信息',
              style: TextStyle(fontSize: 14, fontWeight: FontWeight.bold),
            ),
          ),
          ...items.map((item) {
            return ListTile(
              leading: ClipRRect(
                borderRadius: BorderRadius.circular(4),
                child: item.product.images?.isNotEmpty == true
                    ? Image.network(
                        item.product.images!.first,
                        width: 60,
                        height: 60,
                        fit: BoxFit.cover,
                      )
                    : Container(
                        width: 60,
                        height: 60,
                        color: Colors.grey[300],
                      ),
              ),
              title: Text(item.product.name),
              subtitle: Text('¥${item.product.price} x ${item.quantity}'),
              trailing: Text(
                '¥${item.subtotal.toStringAsFixed(2)}',
                style: TextStyle(fontWeight: FontWeight.bold),
              ),
            );
          }).toList(),
        ],
      ),
    );
  }

  /// 构建优惠券区域
  Widget _buildCouponSection() {
    return Container(
      margin: EdgeInsets.all(12),
      padding: EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(8),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            '优惠券',
            style: TextStyle(fontSize: 14, fontWeight: FontWeight.bold),
          ),
          SizedBox(height: 12),
          if (_availableCoupons.isEmpty)
            Text(
              '暂无可用优惠券',
              style: TextStyle(color: Colors.grey),
            )
          else
            ..._availableCoupons.map((coupon) {
              final isSelected = _selectedCoupon?.id == coupon.id;
              
              return GestureDetector(
                onTap: () {
                  setState(() {
                    _selectedCoupon = isSelected ? null : coupon;
                  });
                },
                child: Container(
                  margin: EdgeInsets.only(bottom: 8),
                  padding: EdgeInsets.all(12),
                  decoration: BoxDecoration(
                    color: isSelected ? Colors.pink.withOpacity(0.1) : Colors.grey[100],
                    borderRadius: BorderRadius.circular(8),
                    border: Border.all(
                      color: isSelected ? Colors.pink : Colors.transparent,
                    ),
                  ),
                  child: Row(
                    children: [
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(
                              coupon.name,
                              style: TextStyle(fontWeight: FontWeight.bold),
                            ),
                            SizedBox(height: 4),
                            Text(
                              coupon.type == 'discount'
                                  ? '${(coupon.discount! * 10).toStringAsFixed(1)}折'
                                  : '满${coupon.minAmount}减${coupon.reductionAmount}',
                              style: TextStyle(color: Colors.grey[600], fontSize: 12),
                            ),
                          ],
                        ),
                      ),
                      if (isSelected)
                        Icon(Icons.check_circle, color: Colors.pink),
                    ],
                  ),
                ),
              );
            }).toList(),
        ],
      ),
    );
  }

  /// 构建备注区域
  Widget _buildRemarkSection() {
    return Container(
      margin: EdgeInsets.symmetric(horizontal: 12),
      padding: EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(8),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            '订单备注',
            style: TextStyle(fontSize: 14, fontWeight: FontWeight.bold),
          ),
          SizedBox(height: 12),
          TextField(
            controller: _remarkController,
            decoration: InputDecoration(
              hintText: '请输入备注信息（选填）',
              border: OutlineInputBorder(),
              contentPadding: EdgeInsets.symmetric(horizontal: 12, vertical: 12),
            ),
            maxLines: 2,
          ),
        ],
      ),
    );
  }

  /// 构建价格区域
  Widget _buildPriceSection() {
    return Container(
      margin: EdgeInsets.all(12),
      padding: EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(8),
      ),
      child: Column(
        children: [
          _buildPriceRow('商品总额', '¥${_productTotal.toStringAsFixed(2)}'),
          SizedBox(height: 8),
          _buildPriceRow('优惠金额', '-¥${_discountAmount.toStringAsFixed(2)}', valueColor: Colors.green),
          SizedBox(height: 8),
          _buildPriceRow('运费', '¥0.00'),
          Divider(height: 24),
          _buildPriceRow(
            '应付金额',
            '¥${_payableAmount.toStringAsFixed(2)}',
            isBold: true,
            valueColor: Colors.red,
          ),
        ],
      ),
    );
  }

  /// 构建价格行
  Widget _buildPriceRow(String label, String value, {
    bool isBold = false,
    Color? valueColor,
  }) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(
          label,
          style: TextStyle(
            fontSize: isBold ? 16 : 14,
            fontWeight: isBold ? FontWeight.bold : FontWeight.normal,
          ),
        ),
        Text(
          value,
          style: TextStyle(
            fontSize: isBold ? 18 : 14,
            fontWeight: isBold ? FontWeight.bold : FontWeight.normal,
            color: valueColor,
          ),
        ),
      ],
    );
  }

  /// 构建底部栏
  Widget _buildBottomBar() {
    return Container(
      padding: EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        boxShadow: [
          BoxShadow(
            color: Colors.black12,
            blurRadius: 8,
            offset: Offset(0, -2),
          ),
        ],
      ),
      child: SafeArea(
        child: Row(
          children: [
            Expanded(
              child: Column(
                mainAxisSize: MainAxisSize.min,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text('应付金额'),
                  Text(
                    '¥${_payableAmount.toStringAsFixed(2)}',
                    style: TextStyle(
                      fontSize: 20,
                      fontWeight: FontWeight.bold,
                      color: Colors.red,
                    ),
                  ),
                ],
              ),
            ),
            ElevatedButton(
              onPressed: _submitOrder,
              style: ElevatedButton.styleFrom(
                backgroundColor: Colors.red,
                foregroundColor: Colors.white,
                padding: EdgeInsets.symmetric(horizontal: 32, vertical: 12),
                minimumSize: Size(120, 48),
              ),
              child: Text('提交订单', style: TextStyle(fontSize: 16)),
            ),
          ],
        ),
      ),
    );
  }

  /// 提交订单
  Future<void> _submitOrder() async {
    // 验证
    if (_nameController.text.isEmpty ||
        _phoneController.text.isEmpty ||
        _addressController.text.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('请填写完整的收货信息')),
      );
      return;
    }

    setState(() => _isLoading = true);

    try {
      final order = await _commerceService.createOrder(
        roomId: _roomId!,
        streamerId: '', // TODO: 获取主播ID
        items: _cartItems,
        couponId: _selectedCoupon?.id,
        remark: _remarkController.text,
        receiverName: _nameController.text,
        receiverPhone: _phoneController.text,
        receiverAddress: _addressController.text,
        deliveryType: _deliveryType,
        pickupPointId: _selectedPickupPointId,
      );

      // 跳转到支付页面
      Navigator.pushNamed(
        context,
        '/live/payment',
        arguments: {'order': order},
      );
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('下单失败: $e')),
      );
    } finally {
      setState(() => _isLoading = false);
    }
  }

  @override
  void dispose() {
    _nameController.dispose();
    _phoneController.dispose();
    _addressController.dispose();
    _remarkController.dispose();
    super.dispose();
  }
}
