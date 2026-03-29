import 'package:flutter/material.dart';
import '../models/coupon_model.dart';

/// 优惠券卡片组件
/// 
/// 展示优惠券的基本信息，支持领取操作
/// 
/// @author IM Development Team
/// @version 1.0
/// @since 2026-03-28
class CouponCard extends StatelessWidget {
  final CouponModel coupon;
  final VoidCallback? onClaim;
  final VoidCallback? onTap;
  final bool showDistance;

  const CouponCard({
    Key? key,
    required this.coupon,
    this.onClaim,
    this.onTap,
    this.showDistance = true,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    // 根据优惠券状态确定颜色
    final isAvailable = coupon.canClaim && !coupon.alreadyClaimed;
    final primaryColor = isAvailable 
        ? const Color(0xFFFF6B6B)
        : Colors.grey;

    return GestureDetector(
      onTap: onTap,
      child: Container(
        margin: const EdgeInsets.only(bottom: 12),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(12),
          boxShadow: [
            BoxShadow(
              color: Colors.black.withOpacity(0.04),
              blurRadius: 8,
              offset: const Offset(0, 2),
            ),
          ],
        ),
        child: ClipRRect(
          borderRadius: BorderRadius.circular(12),
          child: Row(
            children: [
              // 左侧金额区域
              _buildAmountSection(primaryColor, isAvailable),
              
              // 中间内容区域
              Expanded(
                child: _buildContentSection(),
              ),
              
              // 右侧领取按钮
              _buildActionSection(primaryColor, isAvailable),
            ],
          ),
        ),
      ),
    );
  }

  /// 金额区域
  Widget _buildAmountSection(Color primaryColor, bool isAvailable) {
    String amountText;
    String unitText;
    
    switch (coupon.couponType) {
      case 'FULL_REDUCTION':
      case 'CASH':
        amountText = coupon.discountValue.toStringAsFixed(0);
        unitText = '元';
        break;
      case 'DISCOUNT':
        amountText = (coupon.discountValue * 10).toStringAsFixed(1);
        unitText = '折';
        break;
      default:
        amountText = coupon.discountValue.toStringAsFixed(0);
        unitText = '元';
    }

    return Container(
      width: 100,
      padding: const EdgeInsets.symmetric(vertical: 16),
      decoration: BoxDecoration(
        gradient: LinearGradient(
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
          colors: isAvailable
              ? [const Color(0xFFFF6B6B), const Color(0xFFFF8E8E)]
              : [Colors.grey.shade300, Colors.grey.shade400],
        ),
      ),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                coupon.couponType == 'DISCOUNT' ? '' : '¥',
                style: const TextStyle(
                  color: Colors.white,
                  fontSize: 16,
                  fontWeight: FontWeight.w500,
                ),
              ),
              Text(
                amountText,
                style: const TextStyle(
                  color: Colors.white,
                  fontSize: 32,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ],
          ),
          const SizedBox(height: 4),
          Text(
            unitText,
            style: TextStyle(
              color: Colors.white.withOpacity(0.9),
              fontSize: 12,
            ),
          ),
          if (coupon.minOrderAmount > 0) ...[
            const SizedBox(height: 4),
            Text(
              '满${coupon.minOrderAmount.toStringAsFixed(0)}可用',
              style: TextStyle(
                color: Colors.white.withOpacity(0.9),
                fontSize: 11,
              ),
            ),
          ],
        ],
      ),
    );
  }

  /// 内容区域
  Widget _buildContentSection() {
    return Padding(
      padding: const EdgeInsets.all(12),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // 商家名称
          Row(
            children: [
              if (coupon.merchantLogo != null)
                ClipOval(
                  child: Image.network(
                    coupon.merchantLogo!,
                    width: 16,
                    height: 16,
                    fit: BoxFit.cover,
                    errorBuilder: (_, __, ___) => const Icon(
                      Icons.store,
                      size: 16,
                      color: Colors.grey,
                    ),
                  ),
                )
              else
                const Icon(Icons.store, size: 16, color: Colors.grey),
              const SizedBox(width: 4),
              Expanded(
                child: Text(
                  coupon.merchantName,
                  style: const TextStyle(
                    fontSize: 13,
                    color: Colors.grey,
                  ),
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                ),
              ),
            ],
          ),
          const SizedBox(height: 6),
          
          // 优惠券标题
          Text(
            coupon.title,
            style: const TextStyle(
              fontSize: 15,
              fontWeight: FontWeight.w600,
              color: Colors.black87,
            ),
            maxLines: 1,
            overflow: TextOverflow.ellipsis,
          ),
          const SizedBox(height: 4),
          
          // 描述
          Text(
            coupon.description,
            style: const TextStyle(
              fontSize: 12,
              color: Colors.grey,
            ),
            maxLines: 1,
            overflow: TextOverflow.ellipsis,
          ),
          const SizedBox(height: 8),
          
          // 有效期和距离
          Row(
            children: [
              // 有效期
              Icon(
                Icons.access_time,
                size: 12,
                color: Colors.grey.shade500,
              ),
              const SizedBox(width: 2),
              Text(
                coupon.validityDesc,
                style: TextStyle(
                  fontSize: 11,
                  color: Colors.grey.shade500,
                ),
              ),
              
              if (showDistance && coupon.distance != null) ...[
                const SizedBox(width: 12),
                Icon(
                  Icons.location_on,
                  size: 12,
                  color: Colors.grey.shade500,
                ),
                const SizedBox(width: 2),
                Text(
                  coupon.distanceDesc ?? '${coupon.distance!.toStringAsFixed(1)}km',
                  style: TextStyle(
                    fontSize: 11,
                    color: Colors.grey.shade500,
                  ),
                ),
              ],
            ],
          ),
          
          // 剩余数量提示
          if (coupon.remainingQuantity < 100) ...[
            const SizedBox(height: 6),
            Text(
              '仅剩${coupon.remainingQuantity}张',
              style: const TextStyle(
                fontSize: 11,
                color: Color(0xFFFF6B6B),
              ),
            ),
          ],
        ],
      ),
    );
  }

  /// 操作按钮区域
  Widget _buildActionSection(Color primaryColor, bool isAvailable) {
    String buttonText;
    VoidCallback? onPressed;
    
    if (coupon.alreadyClaimed) {
      buttonText = '已领取';
      onPressed = null;
    } else if (!coupon.canClaim) {
      buttonText = '已抢完';
      onPressed = null;
    } else {
      buttonText = '立即领取';
      onPressed = onClaim;
    }

    return Container(
      width: 80,
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        border: Border(
          left: BorderSide(
            color: Colors.grey.shade200,
            width: 1,
            style: BorderStyle.dashed,
          ),
        ),
      ),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          // 虚线装饰
          CustomPaint(
            size: const Size(1, 20),
            painter: DashedLinePainter(color: Colors.grey.shade300),
          ),
          const SizedBox(height: 8),
          
          // 领取按钮
          SizedBox(
            width: 64,
            height: 28,
            child: ElevatedButton(
              onPressed: onPressed,
              style: ElevatedButton.styleFrom(
                backgroundColor: isAvailable ? primaryColor : Colors.grey.shade300,
                foregroundColor: Colors.white,
                elevation: 0,
                padding: EdgeInsets.zero,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(14),
                ),
              ),
              child: Text(
                buttonText,
                style: const TextStyle(
                  fontSize: 12,
                  fontWeight: FontWeight.w500,
                ),
              ),
            ),
          ),
          const SizedBox(height: 8),
          
          // 已领数量
          Text(
            '已领${coupon.claimedCount}',
            style: TextStyle(
              fontSize: 10,
              color: Colors.grey.shade400,
            ),
          ),
        ],
      ),
    );
  }
}

/// 虚线画笔
class DashedLinePainter extends CustomPainter {
  final Color color;

  DashedLinePainter({required this.color});

  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint()
      ..color = color
      ..strokeWidth = 1
      ..style = PaintingStyle.stroke;

    const dashHeight = 4;
    const dashSpace = 3;
    double startY = 0;

    while (startY < size.height) {
      canvas.drawLine(
        Offset(size.width / 2, startY),
        Offset(size.width / 2, startY + dashHeight),
        paint,
      );
      startY += dashHeight + dashSpace;
    }
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false;
}
