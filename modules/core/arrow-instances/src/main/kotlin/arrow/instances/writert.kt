package arrow.instances

import arrow.Kind
import arrow.core.Either
import arrow.core.toT
import arrow.data.WriterT
import arrow.data.WriterTKind
import arrow.data.WriterTKindPartial
import arrow.data.reify
import arrow.instance
import arrow.typeclasses.*

@instance(WriterT::class)
interface WriterTFunctorInstance<F, W> : Functor<WriterTKindPartial<F, W>> {
    fun FF(): Functor<F>

    override fun <A, B> map(fa: WriterTKind<F, W, A>, f: (A) -> B): WriterT<F, W, B> = fa.reify().map({ f(it) }, FF())
}

@instance(WriterT::class)
interface WriterTApplicativeInstance<F, W> : Applicative<WriterTKindPartial<F, W>>, WriterTFunctorInstance<F, W> {

    override fun FF(): Monad<F>

    fun MM(): Monoid<W>

    override fun <A> pure(a: A): WriterTKind<F, W, A> =
            WriterT(FF().pure(MM().empty() toT a))

    override fun <A, B> ap(fa: WriterTKind<F, W, A>, ff: Kind<WriterTKindPartial<F, W>, (A) -> B>): WriterT<F, W, B> =
            fa.reify().ap(ff, MM(), FF())

    override fun <A, B> map(fa: WriterTKind<F, W, A>, f: (A) -> B): WriterT<F, W, B> =
            fa.reify().map({ f(it) }, FF())
}

@instance(WriterT::class)
interface WriterTMonadInstance<F, W> : WriterTApplicativeInstance<F, W>, Monad<WriterTKindPartial<F, W>> {

    override fun <A, B> map(fa: WriterTKind<F, W, A>, f: (A) -> B): WriterT<F, W, B> =
            fa.reify().map({ f(it) }, FF())

    override fun <A, B> flatMap(fa: WriterTKind<F, W, A>, f: (A) -> Kind<WriterTKindPartial<F, W>, B>): WriterT<F, W, B> =
            fa.reify().flatMap({ f(it).reify() }, MM(), FF())

    override fun <A, B> tailRecM(a: A, f: (A) -> Kind<WriterTKindPartial<F, W>, Either<A, B>>): WriterT<F, W, B> =
            WriterT.tailRecM(a, f, FF())

    override fun <A, B> ap(fa: WriterTKind<F, W, A>, ff: Kind<WriterTKindPartial<F, W>, (A) -> B>): WriterT<F, W, B> =
            fa.reify().ap(ff, MM(), FF())
}

@instance(WriterT::class)
interface WriterTSemigroupKInstance<F, W> : SemigroupK<WriterTKindPartial<F, W>> {

    fun SS(): SemigroupK<F>

    override fun <A> combineK(x: WriterTKind<F, W, A>, y: WriterTKind<F, W, A>): WriterT<F, W, A> =
            x.reify().combineK(y, SS())
}

@instance(WriterT::class)
interface WriterTMonoidKInstance<F, W> : MonoidK<WriterTKindPartial<F, W>>, WriterTSemigroupKInstance<F, W> {

    override fun SS(): MonoidK<F>

    override fun <A> empty(): WriterT<F, W, A> = WriterT(SS().empty())
}